package com.bookstore.api.domain.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // UPCOMING → VIP_OPEN
    List<Sale> findByVipOpenAtBeforeAndSaleStatus(LocalDateTime now, SaleStatus status);

    // UPCOMING or VIP_OPEN → ON_SALE
    List<Sale> findByGeneralOpenAtBeforeAndSaleStatusIn(LocalDateTime now, List<SaleStatus> statuses);

    // ON_SALE → CLOSED
    List<Sale> findByCloseAtBeforeAndSaleStatus(LocalDateTime now, SaleStatus status);
}
