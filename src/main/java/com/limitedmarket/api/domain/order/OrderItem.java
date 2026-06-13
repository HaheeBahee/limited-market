package com.limitedmarket.api.domain.order;

import com.limitedmarket.api.domain.sale.Sale;
import com.limitedmarket.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    public static OrderItem create(
            Order order,
            Sale sale,
            int quantity
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.order = order;
        orderItem.sale = sale;
        orderItem.unitPrice = sale.getSalePrice();
        orderItem.quantity = quantity;
        return orderItem;
    }
}
