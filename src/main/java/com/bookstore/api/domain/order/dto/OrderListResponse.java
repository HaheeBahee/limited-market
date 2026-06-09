package com.bookstore.api.domain.order.dto;

import com.bookstore.api.domain.order.Order;
import com.bookstore.api.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderListResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "PENDING", allowableValues = {"PENDING", "PAID", "CANCELLED", "FAILED"}) OrderStatus orderStatus,
        @Schema(example = "30000") BigDecimal totalPrice,
        LocalDateTime createdAt
) {
    public static OrderListResponse from(Order order) {
        return new OrderListResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
