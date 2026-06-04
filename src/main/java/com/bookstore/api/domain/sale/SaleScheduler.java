package com.bookstore.api.domain.sale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaleScheduler {

    private final SaleRepository saleRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void updateSaleStatus() {
        LocalDateTime now = LocalDateTime.now();

        // UPCOMING → VIP_OPEN
        saleRepository.findByVipOpenAtBeforeAndSaleStatus(now, SaleStatus.UPCOMING)
                .forEach(Sale::openForVip);

        // UPCOMING or VIP_OPEN → ON_SALE
        saleRepository.findByGeneralOpenAtBeforeAndSaleStatusIn(
                        now, List.of(SaleStatus.UPCOMING, SaleStatus.VIP_OPEN))
                .forEach(Sale::openForGeneral);

        // ON_SALE → CLOSED
        saleRepository.findByCloseAtBeforeAndSaleStatus(now, SaleStatus.ON_SALE)
                .forEach(Sale::close);

        log.info("[SaleScheduler] 판매 상태 업데이트 완료 - {}", now);

    }
}
