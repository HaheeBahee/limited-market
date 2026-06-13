package com.limitedmarket.api.domain.delivery;

import com.limitedmarket.api.domain.order.Order;
import com.limitedmarket.api.domain.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public void create(Order order, PaymentRequest request) {
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
