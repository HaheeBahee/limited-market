package com.bookstore.api.domain.order;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.order.dto.OrderCancelledEvent;
import com.bookstore.api.domain.order.dto.OrderCreateRequest;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.domain.order.dto.OrderItemRequest;
import com.bookstore.api.domain.sale.RedisStockService;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final RedisStockService redisStockService;
    private final OrderCreateService orderCreateService;
    private final ApplicationEventPublisher eventPublisher;

    // 주문 생성
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

        // 3. saleId별 요청 수량을 빠르게 조회하기 위해 Map으로 변환
        Map<Long, Integer> quantityBySaleId = request.items().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::saleId,
                        OrderItemRequest::quantity
                ));

        // 4. Redis에서 먼저 재고 차감 (DB 트랜잭션 시작 전)
        List<Long> decreasedSaleIds = new ArrayList<>();
        try {
            for (Long saleId : saleIds) {
                redisStockService.decrease(saleId, quantityBySaleId.get(saleId));
                decreasedSaleIds.add(saleId);
            }
        } catch (CustomException e) {
            for (Long saleId : decreasedSaleIds) {
                redisStockService.restore(saleId, quantityBySaleId.get(saleId));
            }
            throw e;
        }

        // 5. DB 트랜잭션
        try {
            return orderCreateService.create(member, saleIds, quantityBySaleId);
        } catch (Exception e) {
            for (Long saleId : saleIds) {
                redisStockService.restore(saleId, quantityBySaleId.get(saleId));
            }
            throw e;
        }
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
        Map<Long, Integer> quantityBySaleId = new HashMap<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            orderItem.getSale().restoreStock(orderItem.getQuantity(), LocalDateTime.now());
            quantityBySaleId.put(orderItem.getSale().getId(), orderItem.getQuantity());
        }
        eventPublisher.publishEvent(new OrderCancelledEvent(new ArrayList<>(quantityBySaleId.keySet()), quantityBySaleId));
    }
}