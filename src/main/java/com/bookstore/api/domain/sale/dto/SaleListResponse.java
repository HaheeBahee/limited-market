package com.bookstore.api.domain.sale.dto;

import com.bookstore.api.domain.sale.Sale;
import com.bookstore.api.domain.sale.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleListResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "한정판 싸인 도서") String productName,
        @Schema(example = "30000") BigDecimal salePrice,
        @Schema(example = "50") int remainQuantity,
        SaleStatus saleStatus,
        LocalDateTime closeAt
) {
    public static SaleListResponse from(Sale sale, int remainQuantity) {
        return new SaleListResponse(
                sale.getId(),
                sale.getProduct().getName(),
                sale.getSalePrice(),
                remainQuantity,
                sale.getSaleStatus(),
                sale.getCloseAt()
        );
    }
}
