package com.limitedmarket.api.domain.order.dto;

import com.limitedmarket.api.domain.order.Order;
import com.limitedmarket.api.domain.order.OrderStatus;

import java.math.BigDecimal;

public record OrderCreateResponse(
        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice
) {
    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(), order.getOrderStatus(), order.getTotalPrice()
        );
    }
}
