package com.limitedmarket.api.domain.order.dto;

import com.limitedmarket.api.domain.order.Order;
import com.limitedmarket.api.domain.order.OrderItem;
import com.limitedmarket.api.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "PENDING", allowableValues = {"PENDING", "PAID", "CANCELLED", "FAILED"}) OrderStatus orderStatus,
        @Schema(example = "30000") BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderDetailResponse from(Order order, List<OrderItem> orderItems) {
        return new OrderDetailResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                orderItems.stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}
