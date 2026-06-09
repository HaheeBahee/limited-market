package com.bookstore.api.domain.order;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.order.dto.OrderCreateRequest;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.domain.order.dto.OrderItemRequest;
import com.bookstore.api.domain.order.dto.OrderDetailResponse;
import com.bookstore.api.domain.order.dto.OrderListResponse;
import com.bookstore.api.domain.payment.Payment;
import com.bookstore.api.domain.payment.PaymentRepository;
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
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final MemberRepository memberRepository;
    private final SaleRepository saleRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public OrderCreateResponse create(OrderCreateRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Long> saleIds = request.items().stream()
                .map(OrderItemRequest::saleId)
                .toList();

        // 중복 saleId 검증
        Set<Long> uniqueIds = new HashSet<>(saleIds);
        if (uniqueIds.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.DUPLICATE_SALE_ITEM);
        }

        List<Sale> sales = saleRepository.findAllByIdWithLock(saleIds);

        if (sales.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.SALE_NOT_FOUND);
        }

        Map<Long, Integer> quantityBySaleId = request.items().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::saleId,
                        OrderItemRequest::quantity
                ));

        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());
            LocalDateTime openAt = sale.getOrderOpenAt(member.getGrade());

            if (now.isBefore(openAt)) throw new CustomException(ErrorCode.SALE_NOT_OPEN);
            if (now.isAfter(sale.getCloseAt())) throw new CustomException(ErrorCode.SALE_CLOSED);
            if (sale.getRemainQuantity() < requestedQuantity) throw new CustomException(ErrorCode.OUT_OF_STOCK);

            totalPrice = totalPrice.add(
                    sale.getSalePrice().multiply(BigDecimal.valueOf(requestedQuantity))
            );
            sale.decreaseStock(requestedQuantity);
        }

        Order order = Order.create(member, totalPrice);
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());
            orderItems.add(OrderItem.create(order, sale, requestedQuantity));
        }
        orderItemRepository.saveAll(orderItems);
        orderStatusHistoryRepository.save(OrderStatusHistory.create(order, OrderStatus.PENDING, null));

        return OrderCreateResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(Long orderId, Long memberId) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        return OrderDetailResponse.from(order, orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderListResponse> getOrders(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(OrderListResponse::from)
                .toList();
    }

    @Transactional
    public void cancel(Long orderId, Long memberId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // PAID 주문이면 배송 상태 먼저 확인
        if (order.getOrderStatus() == OrderStatus.PAID) {
            Delivery delivery = deliveryRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
            if (!delivery.isCancellable()) {
                throw new CustomException(ErrorCode.ORDER_CANCEL_FAILED);
            }
            delivery.cancel();

            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
            payment.refund();
        }

        order.cancel();
        orderStatusHistoryRepository.save(OrderStatusHistory.create(order, OrderStatus.CANCELLED, null));

        // 재고 복구
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            orderItem.getSale().restoreStock(orderItem.getQuantity(), LocalDateTime.now());
        }
    }
}