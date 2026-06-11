package com.limitedmarket.api.domain.product;

import com.limitedmarket.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_code",
                columnNames = "product_code"
        )
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus productStatus = ProductStatus.INACTIVE; // ACTIVE, INACTIVE


    public static Product create(
            String productCode,
            String name,
            String description,
            BigDecimal price,
            ProductType productType,
            ProductStatus productStatus
    ) {
        Product product = new Product();
        product.productCode = productCode;
        product.name = name;
        product.description = description;
        product.price = price;
        product.productType = productType;
        product.productStatus = productStatus;
        return product;
    }

    public void activate() {
        this.productStatus = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.productStatus = ProductStatus.INACTIVE;
    }
}
