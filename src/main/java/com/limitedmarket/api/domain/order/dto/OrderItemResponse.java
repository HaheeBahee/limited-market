package com.limitedmarket.api.domain.order.dto;

import com.limitedmarket.api.domain.order.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderItemResponse(
        @Schema(example = "나이키 에어포스 1 한정판 컬래버") String productName,
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
