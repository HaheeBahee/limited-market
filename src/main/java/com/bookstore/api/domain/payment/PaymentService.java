package com.bookstore.api.domain.payment;

import com.bookstore.api.domain.order.Order;
import com.bookstore.api.domain.order.OrderRepository;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void pay(Long orderId, Long memberId){

        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 본인 주문인지 확인
        if(!order.getMember().getId().equals(memberId)){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 3. Payment 생성 및 저장
        Payment payment = Payment.create(order, order.getTotalPrice());
        paymentRepository.save(payment);

        // 4. Order 상태 변경 PENDING -> PAID
        order.pay();
    }

}
