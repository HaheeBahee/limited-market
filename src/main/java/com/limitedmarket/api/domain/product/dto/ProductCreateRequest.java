package com.limitedmarket.api.domain.product.dto;

import com.limitedmarket.api.domain.product.ProductStatus;
import com.limitedmarket.api.domain.product.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @Schema(example = "LM-001")
        @NotBlank(message = "상품 코드는 필수입니다")
        String productCode,

        @Schema(example = "나이키 에어포스 1 한정판 컬래버")
        @NotBlank(message = "상품명은 필수입니다")
        String name,

        @Schema(example = "나이키 x 오프화이트 한정 100켤레 드롭")
        String description,

        @Schema(example = "30000")
        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 0보다 커야 합니다")
        BigDecimal price,

        @Schema(example = "SIGNED")
        @NotNull(message = "상품 타입은 필수입니다")
        ProductType productType,

        @Schema(example = "ACTIVE")
        @NotNull(message = "상품 상태는 필수입니다")
        ProductStatus productStatus
) {
}
