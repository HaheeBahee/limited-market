package com.bookstore.api.domain.product.dto;

import com.bookstore.api.domain.product.Product;
import com.bookstore.api.domain.product.ProductStatus;
import com.bookstore.api.domain.product.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        ProductType productType,
        ProductStatus productStatus,
        LocalDateTime createdAt
) {
    public static ProductDetailResponse from(Product product){
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getProductType(),
                product.getProductStatus(),
                product.getCreatedAt()
        );
    }
}
