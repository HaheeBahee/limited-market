package com.bookstore.api.domain.order.dto;

import com.bookstore.api.domain.order.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderItemResponse(
        @Schema(example = "한정판 싸인 도서") String productName,
        @Schema(example = "1") int quantity,
        @Schema(example = "30000") BigDecimal unitPrice
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getSale().getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice()
        );
    }
}
