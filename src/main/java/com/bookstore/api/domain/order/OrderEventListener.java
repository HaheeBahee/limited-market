package com.bookstore.api.domain.order;

import com.bookstore.api.domain.order.dto.OrderCancelledEvent;
import com.bookstore.api.domain.sale.RedisStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final RedisStockService redisStockService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        for (Long saleId : event.saleIds()) {
            try {
                redisStockService.restore(saleId, event.quantityBySaleId().get(saleId));
            } catch (Exception e) {
                log.error("Redis 재고 복구 실패 - saleId: {}, quantity: {}",
                        saleId, event.quantityBySaleId().get(saleId), e);
            }
        }
    }
}
