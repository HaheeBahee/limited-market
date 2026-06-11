package com.limitedmarket.api.domain.delivery;

import com.limitedmarket.api.domain.order.Order;
import com.limitedmarket.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String zipcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus; // PREPARING, SHIPPING, DELIVERED, CANCELLED

    public static Delivery create(Order order, String recipientName, String phone,
                                   String city, String street, String zipcode) {
        Delivery delivery = new Delivery();
        delivery.order = order;
        delivery.recipientName = recipientName;
        delivery.phone = phone;
        delivery.city = city;
        delivery.street = street;
        delivery.zipcode = zipcode;
        delivery.deliveryStatus = DeliveryStatus.PREPARING;
        return delivery;
    }

    public boolean isCancellable() {
        return this.deliveryStatus == DeliveryStatus.PREPARING;
    }

    public void cancel() {
        this.deliveryStatus = DeliveryStatus.CANCELLED;
    }
}
