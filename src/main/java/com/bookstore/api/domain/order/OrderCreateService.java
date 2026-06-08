package com.bookstore.api.domain.order;

import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.domain.sale.Sale;
import com.bookstore.api.domain.sale.SaleRepository;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public OrderCreateResponse create(Member member, List<Long> saleIds, Map<Long, Integer> quantityBySaleId) {

        List<Sale> sales = saleRepository.findAllById(saleIds);

        if (sales.size() != saleIds.size()) {
            throw new CustomException(ErrorCode.SALE_NOT_FOUND);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();

        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());

            LocalDateTime openAt = sale.getOrderOpenAt(member.getGrade());

            if (now.isBefore(openAt)) {
                throw new CustomException(ErrorCode.SALE_NOT_OPEN);
            }

            if (now.isAfter(sale.getCloseAt())) {
                throw new CustomException(ErrorCode.SALE_CLOSED);
            }

            sale.decreaseStock(requestedQuantity);

            totalPrice = totalPrice.add(
                    sale.getSalePrice().multiply(BigDecimal.valueOf(requestedQuantity))
            );
        }

        Order order = Order.create(member, totalPrice);
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Sale sale : sales) {
            int requestedQuantity = quantityBySaleId.get(sale.getId());
            orderItems.add(OrderItem.create(order, sale, requestedQuantity));
        }
        orderItemRepository.saveAll(orderItems);

        return OrderCreateResponse.from(order);
    }
}
