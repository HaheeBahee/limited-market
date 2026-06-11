package com.limitedmarket.api.domain.payment;

import com.limitedmarket.api.domain.order.Order;
import com.limitedmarket.api.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(unique = true, nullable = false)
    private String impUid;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    public void refund() {
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    public static Payment create(Order order, BigDecimal amount, String impUid) {
        Payment payment = new Payment();
        payment.order = order;
        payment.impUid = impUid;  // 실제 포트원 연동 시 교체
        payment.amount = amount;
        payment.paymentStatus = PaymentStatus.PAID;
        return payment;
    }
}
