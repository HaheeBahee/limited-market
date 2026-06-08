package com.bookstore.api.domain.sale;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // 비관적 락 - SELECT FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sale s WHERE s.id IN :ids")
    List<Sale> findAllByIdWithLock(@Param("ids") List<Long> ids);

    // UPCOMING → VIP_OPEN
    List<Sale> findByVipOpenAtBeforeAndSaleStatus(LocalDateTime now, SaleStatus status);

    // UPCOMING or VIP_OPEN → ON_SALE
    List<Sale> findByGeneralOpenAtBeforeAndSaleStatusIn(LocalDateTime now, List<SaleStatus> statuses);

    // ON_SALE → CLOSED
    List<Sale> findByCloseAtBeforeAndSaleStatus(LocalDateTime now, SaleStatus status);
}
