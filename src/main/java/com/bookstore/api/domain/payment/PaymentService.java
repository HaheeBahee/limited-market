package com.bookstore.api.domain.payment;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.order.*;
import com.bookstore.api.domain.payment.dto.PaymentRequest;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Transactional
    public void pay(Long orderId, Long memberId, PaymentRequest request) {

        // 동시 결제 요청 방지 - 비관적 락으로 단일 처리
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // TODO: 실제 포트원 연동 시 request.impUid()로 PG사 결제 정보 조회 후 amount 비교
        if (request.amount().compareTo(order.getTotalPrice()) != 0) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        order.markAsPaid();

        Payment payment = Payment.create(order, order.getTotalPrice(), request.impUid());
        paymentRepository.save(payment);
        orderStatusHistoryRepository.save(OrderStatusHistory.create(order, OrderStatus.PAID, null));

        Delivery delivery = Delivery.create(
                order,
                request.deliveryRequest().recipientName(),
                request.deliveryRequest().phone(),
                request.deliveryRequest().city(),
                request.deliveryRequest().street(),
                request.deliveryRequest().zipcode()
        );
        deliveryRepository.save(delivery);
    }

}
