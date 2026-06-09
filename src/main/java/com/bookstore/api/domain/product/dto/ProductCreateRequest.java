package com.bookstore.api.domain.product.dto;

import com.bookstore.api.domain.product.ProductStatus;
import com.bookstore.api.domain.product.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @Schema(example = "BOOK-001")
        @NotBlank(message = "상품 코드는 필수입니다")
        String productCode,

        @Schema(example = "한정판 싸인 도서")
        @NotBlank(message = "상품명은 필수입니다")
        String name,

        @Schema(example = "작가 직접 싸인본 한정 100권")
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
