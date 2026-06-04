package com.bookstore.api.domain.sale;

import com.bookstore.api.domain.product.Product;
import com.bookstore.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

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


    public static Sale create(
            Product product,
            BigDecimal salePrice,
            int totalQuantity,
            LocalDateTime vipOpenAt,
            LocalDateTime generalOpenAt,
            LocalDateTime closeAt
    ) {
        Sale sale = new Sale();
        sale.product = product;
        sale.salePrice = salePrice;
        sale.totalQuantity = totalQuantity;
        sale.remainQuantity = totalQuantity;
        sale.vipOpenAt = vipOpenAt;
        sale.generalOpenAt = generalOpenAt;
        sale.closeAt = closeAt;
        sale.saleStatus = SaleStatus.UPCOMING;

        return sale;
    }

    public void openForVip() {
        this.saleStatus = SaleStatus.VIP_OPEN;
    }

    public void openForGeneral() {
        this.saleStatus = SaleStatus.ON_SALE;
    }

    public void soldOut() {
        this.saleStatus = SaleStatus.SOLD_OUT;
    }

    public void close() {
        this.saleStatus = SaleStatus.CLOSED;
    }

    public void decreaseStock(int quantity) {
        this.remainQuantity -= quantity;
    }

    public void restoreStock(int quantity) {
        this.remainQuantity += quantity;
    }
}
