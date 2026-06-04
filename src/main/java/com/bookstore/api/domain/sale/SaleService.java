package com.bookstore.api.domain.sale;

import com.bookstore.api.domain.product.Product;
import com.bookstore.api.domain.product.ProductRepository;
import com.bookstore.api.domain.sale.dto.SaleCreateRequest;
import com.bookstore.api.domain.sale.dto.SaleCreateResponse;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    // 세일 생성
    @Transactional
    public SaleCreateResponse create(SaleCreateRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        Sale sale = Sale.create(product,
                request.salePrice(),
                request.totalQuantity(),
                request.vipOpenAt(),
                request.generalOpenAt(),
                request.closeAt());

        saleRepository.save(sale);
        return SaleCreateResponse.from(sale);
    }
}
