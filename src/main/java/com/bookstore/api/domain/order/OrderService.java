package com.bookstore.api.domain.order;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.order.dto.OrderCancelledEvent;
import com.bookstore.api.domain.order.dto.OrderCreateRequest;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.domain.order.dto.OrderDetailResponse;
import com.bookstore.api.domain.order.dto.OrderItemRequest;
import com.bookstore.api.domain.order.dto.OrderListResponse;
import com.bookstore.api.domain.order.dto.OrderStatusHistoryResponse;
import com.bookstore.api.domain.payment.Payment;
import com.bookstore.api.domain.payment.PaymentRepository;
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
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final RedisStockService redisStockService;
    private final OrderCreateService orderCreateService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderCreateResponse create(OrderCreateRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Long> saleIds = request.items().stream()
                .map(OrderItemRequest::saleId)
                .toList();

        Set<Long> uniqueIds = new HashSet<>(saleIds);
        if (uniqueIds.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.DUPLICATE_SALE_ITEM);
        }

        Map<Long, Integer> quantityBySaleId = request.items().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::saleId,
                        OrderItemRequest::quantity
                ));

        // Redis에서 먼저 재고 차감
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

        // DB 트랜잭션
        try {
            return orderCreateService.create(member, saleIds, quantityBySaleId);
        } catch (Exception e) {
            for (Long saleId : saleIds) {
                redisStockService.restore(saleId, quantityBySaleId.get(saleId));
            }
            throw e;
        }
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

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderHistory(Long orderId, Long memberId) {
        // 본인 주문인지 확인
        orderRepository.findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        return orderStatusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId).stream()
                .map(OrderStatusHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void cancel(Long orderId, Long memberId) {

        // 동시 취소 요청 방지 - 비관적 락으로 단일 트랜잭션만 처리
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (order.getOrderStatus() == OrderStatus.PAID) {
            // PAID 상태에서는 배송, 결제 정보가 반드시 존재해야 함
            // 없다면 데이터 정합성 오류
            Delivery delivery = deliveryRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_ERROR));
            if (!delivery.isCancellable()) {
                throw new CustomException(ErrorCode.ORDER_CANCEL_FAILED);
            }
            delivery.cancel();

            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_ERROR));
            payment.refund();
        }

        order.cancel();
        orderStatusHistoryRepository.save(OrderStatusHistory.create(order, OrderStatus.CANCELLED, null));

        Map<Long, Integer> quantityBySaleId = new HashMap<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            orderItem.getSale().restoreStock(orderItem.getQuantity(), LocalDateTime.now());
            quantityBySaleId.put(orderItem.getSale().getId(), orderItem.getQuantity());
        }
        eventPublisher.publishEvent(new OrderCancelledEvent(new ArrayList<>(quantityBySaleId.keySet()), quantityBySaleId));
    }
}
