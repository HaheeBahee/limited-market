package com.bookstore.api.domain.order;

public enum OrderStatus {
    PENDING,    // 주문 생성됨, 결제 전
    PAID,       // 결제 완료
    CANCELLED,  // 취소됨
    FAILED      // 결제 실패
}
