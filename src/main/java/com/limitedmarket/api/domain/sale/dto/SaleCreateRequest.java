package com.limitedmarket.api.domain.sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleCreateRequest(

        @Schema(example = "1")
        @NotNull(message = "상품 ID는 필수입니다")
        Long productId,

        @Schema(example = "30000")
        @NotNull(message = "판매 가격은 필수입니다")
        @Positive
        BigDecimal salePrice,

        @Schema(example = "100")
        @Positive(message = "수량은 0보다 커야 합니다")
        int totalQuantity,

        @Schema(example = "2026-06-09T09:00:00")
        @NotNull(message = "VIP 오픈 시간은 필수입니다")
        LocalDateTime vipOpenAt,

        @Schema(example = "2026-06-09T10:00:00")
        @NotNull(message = "오픈 시간은 필수입니다")
        LocalDateTime generalOpenAt,

        @Schema(example = "2026-12-31T23:59:59")
        @NotNull(message = "종료 시간은 필수입니다")
        LocalDateTime closeAt
) {
}
