package com.bookstore.api.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(

        @NotNull
        Long saleId,

        @Min(1)
        int quantity
) {
}