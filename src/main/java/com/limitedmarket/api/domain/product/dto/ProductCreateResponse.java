package com.limitedmarket.api.domain.product.dto;

import com.limitedmarket.api.domain.product.Product;
import com.limitedmarket.api.domain.product.ProductStatus;
import com.limitedmarket.api.domain.product.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductCreateResponse(
        Long id,
        String productCode,
        String name,
        String description,
        BigDecimal price,
        ProductType productType,
        ProductStatus productStatus,
        LocalDateTime createdAt
) {
    public static ProductCreateResponse from(Product product){
        return new ProductCreateResponse(
                product.getId(),
                product.getProductCode(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getProductType(),
                product.getProductStatus(),
                product.getCreatedAt()
        );
    }
}
