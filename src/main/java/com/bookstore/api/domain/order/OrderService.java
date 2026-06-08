package com.bookstore.api.domain.order;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.order.dto.OrderCreateRequest;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.domain.order.dto.OrderItemRequest;
import com.bookstore.api.domain.sale.Sale;
import com.bookstore.api.domain.sale.SaleRepository;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final SaleRepository saleRepository;
    private final DeliveryRepository deliveryRepository;

    // 주문 생성

    @Transactional
    public OrderCreateResponse create(OrderCreateRequest request, Long memberId) {

        // 1. Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. SaleId 추출
        List<Long> saleIds = request.items().stream()
                .map(OrderItemRequest::saleId)
                .toList();

        // 중복 SaldId 검증
        Set<Long> uniqueIds = new HashSet<>(saleIds);

        if (uniqueIds.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.DUPLICATE_SALE_ITEM);
        }

        // Sale 조회 (비관적 락 - SELECT FOR UPDATE)
        List<Sale> sales = saleRepository.findAllByIdWithLock(saleIds);

        // 존재 여부 검증
        if (sales.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.SALE_NOT_FOUND);
        }

        // 3. saleId별 요청 수량을 빠르게 조회하기 위해 Map으로 변환
        Map<Long, Integer> quantityBySaleId = request.items().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::saleId,
                        OrderItemRequest::quantity
                ));

        // 4. 검증 + 금액 계산 + 재고 차감
        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());

            LocalDateTime openAt = sale.getOrderOpenAt(member.getGrade());

            // 판매 시작 전
            if (now.isBefore(openAt)) {
                throw new CustomException(ErrorCode.SALE_NOT_OPEN);
            }

            // 판매 종료
            if (now.isAfter(sale.getCloseAt())) {
                throw new CustomException(ErrorCode.SALE_CLOSED);
            }

            // 재고 부족
            if (sale.getRemainQuantity() < requestedQuantity) {
                throw new CustomException(ErrorCode.OUT_OF_STOCK);
            }


            totalPrice = totalPrice.add(
                    sale.getSalePrice().multiply(BigDecimal.valueOf(requestedQuantity))
            );

            // DB 재고 차감 - 추후 Redis DECR 방식으로 교체 예정
            sale.decreaseStock(requestedQuantity);
        }

        // 5. 검증 통과 후 Order 저장
        Order order = Order.create(member, totalPrice);
        orderRepository.save(order);

        // 6. OrderItem 생성 후 일괄 저장
        List<OrderItem> orderItems = new ArrayList<>();
        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());
            orderItems.add(OrderItem.create(order, sale, requestedQuantity));
        }
        orderItemRepository.saveAll(orderItems);

        return OrderCreateResponse.from(order);
    }

    // 주문 취소 - 근데 여기도 취소 성공 메시지라도 보내야하는거 아닌가
    @Transactional
    public void cancel(Long orderId, Long memberId) {

        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!order.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // PAID 주문이면 배송 상태 확인
        if (order.getOrderStatus() == OrderStatus.PAID) {
            Delivery delivery = deliveryRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
            if (!delivery.isCancellable()) {
                throw new CustomException(ErrorCode.ORDER_CANCEL_FAILED);
            }
        }

        // 취소 가능 상태인지 확인 + 상태 변경
        order.cancel();

        // OrderItem 조회 -> 재고 복구
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            orderItem.getSale().restoreStock(orderItem.getQuantity(), LocalDateTime.now());
        }
    }
}