package com.bookstore.api.domain.product.dto;

import com.bookstore.api.domain.product.Product;
import com.bookstore.api.domain.product.ProductStatus;
import com.bookstore.api.domain.product.ProductType;

import java.math.BigDecimal;

public record ProductListResponse(
        Long id,
        String name,
        BigDecimal price,
        ProductType productType,
        ProductStatus productStatus
){
    public static ProductListResponse from(Product product){
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getProductType(),
                product.getProductStatus()
        );
    }
}
