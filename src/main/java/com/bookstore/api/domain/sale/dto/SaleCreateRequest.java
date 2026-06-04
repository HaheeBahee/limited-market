package com.bookstore.api.domain.sale.dto;

import com.bookstore.api.domain.product.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleCreateRequest(

        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @NotNull(message = "판매 가격은 필수입니다")
        @Positive
        BigDecimal salePrice,

        @Positive(message = "수량은 0보다 커야 합니다")
        int totalQuantity,

        LocalDateTime vipOpenAt,

        @NotNull(message = "오픈 시간은 필수입니다")
        LocalDateTime generalOpenAt,

        @NotNull(message = "종료 시간은 필수입니다")
        LocalDateTime closeAt
) {
}
