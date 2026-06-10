package com.bookstore.api.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(

        @Schema(example = "1")
        @NotNull
        Long saleId,

        @Schema(example = "1")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
        int quantity
) {
}