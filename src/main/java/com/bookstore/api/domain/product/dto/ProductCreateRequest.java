package com.bookstore.api.domain.product.dto;

import com.bookstore.api.domain.product.ProductStatus;
import com.bookstore.api.domain.product.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank(message = "상품 코드는 필수입니다")
        String productCode,

        @NotBlank(message = "상품명은 필수입니다")
        String name,

        String description,

        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 0보다 커야 합니다")
        BigDecimal price,

        @NotNull(message = "상품 타입은 필수입니다")
        ProductType productType,

        @NotNull(message = "상품 상태는 필수입니다")
        ProductStatus productStatus
) {
}
