package com.limitedmarket.api.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.sale WHERE oi.order = :order")
    List<OrderItem> findByOrderWithSale(@Param("order") Order order);
}
