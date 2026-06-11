package com.limitedmarket.api.domain.sale.dto;

import com.limitedmarket.api.domain.sale.Sale;
import com.limitedmarket.api.domain.sale.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleCreateResponse(
        Long id,
        Long productId,
        BigDecimal salePrice,
        int totalQuantity,
        int remainQuantity,
        LocalDateTime vipOpenAt,
        LocalDateTime generalOpenAt,
        LocalDateTime closeAt,
        SaleStatus saleStatus
) {
    public static SaleCreateResponse from(Sale sale) {
        return new SaleCreateResponse(
                sale.getId(),
                sale.getProduct().getId(),
                sale.getSalePrice(),
                sale.getTotalQuantity(),
                sale.getRemainQuantity(),
                sale.getVipOpenAt(),
                sale.getGeneralOpenAt(),
                sale.getCloseAt(),
                sale.getSaleStatus()
        );
    }
}
