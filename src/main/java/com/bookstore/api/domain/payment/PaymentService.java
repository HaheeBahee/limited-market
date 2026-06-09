package com.bookstore.api.domain.payment;

import com.bookstore.api.domain.delivery.Delivery;
import com.bookstore.api.domain.delivery.DeliveryRepository;
import com.bookstore.api.domain.order.Order;
import com.bookstore.api.domain.order.OrderRepository;
import com.bookstore.api.domain.order.OrderStatus;
import com.bookstore.api.domain.order.OrderStatusHistory;
import com.bookstore.api.domain.order.OrderStatusHistoryRepository;
import com.bookstore.api.domain.delivery.dto.DeliveryRequest;
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
    public void pay(Long orderId, Long memberId, DeliveryRequest deliveryRequest) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        order.markAsPaid();

        Payment payment = Payment.create(order, order.getTotalPrice());
        paymentRepository.save(payment);
        orderStatusHistoryRepository.save(OrderStatusHistory.create(order, OrderStatus.PAID, null));

        Delivery delivery = Delivery.create(
                order,
                deliveryRequest.recipientName(),
                deliveryRequest.phone(),
                deliveryRequest.city(),
                deliveryRequest.street(),
                deliveryRequest.zipcode()
        );
        deliveryRepository.save(delivery);
    }

}
