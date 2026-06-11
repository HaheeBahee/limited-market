package com.limitedmarket.api.domain.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(

        @NotNull
        @Size(min = 1)
        List<OrderItemRequest> items
) {
}
