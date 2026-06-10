package com.bookstore.api.domain.order.dto;

import com.bookstore.api.domain.order.OrderStatus;
import com.bookstore.api.domain.order.OrderStatusHistory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record OrderStatusHistoryResponse(
        @Schema(example = "PENDING") OrderStatus status,
        @Schema(example = "사용자 요청으로 취소") String reason,
        LocalDateTime changedAt
) {
    public static OrderStatusHistoryResponse from(OrderStatusHistory history) {
        return new OrderStatusHistoryResponse(
                history.getStatus(),
                history.getReason(),
                history.getCreatedAt()
        );
    }
}
