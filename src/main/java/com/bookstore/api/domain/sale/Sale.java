package com.bookstore.api.domain.sale;

import com.bookstore.api.domain.product.Product;
import com.bookstore.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remainQuantity;

    private LocalDateTime vipOpenAt;

    @Column(nullable = false)
    private LocalDateTime generalOpenAt;

    @Column(nullable = false)
    private LocalDateTime closeAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus saleStatus = SaleStatus.UPCOMING; // UPCOMING, VIP_OPEN, ON_SALE, SOLD_OUT, CLOSED

}
