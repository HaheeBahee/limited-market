package com.limitedmarket.api.domain.order.dto;

import java.util.List;
import java.util.Map;

public record OrderCancelledEvent(
        List<Long> saleIds,
        Map<Long, Integer> quantityBySaleId
) {
}
