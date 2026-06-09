package com.bookstore.api.domain.order;

import com.bookstore.api.domain.order.dto.OrderCancelledEvent;
import com.bookstore.api.domain.sale.RedisStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final RedisStockService redisStockService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        for (Long saleId : event.saleIds()) {
            redisStockService.restore(saleId, event.quantityBySaleId().get(saleId));
        }
    }
}
