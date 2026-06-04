package com.bookstore.api.domain.product;

import com.bookstore.api.domain.product.dto.ProductCreateRequest;
import com.bookstore.api.domain.product.dto.ProductCreateResponse;
import com.bookstore.api.domain.product.dto.ProductDetailResponse;
import com.bookstore.api.domain.product.dto.ProductListResponse;
import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 등록
    @Transactional
    public ProductCreateResponse registerProduct(ProductCreateRequest request) {

        if (productRepository.existsByProductCode(request.productCode())) {
            throw new CustomException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }

        Product product = Product.create(request.productCode(),
                request.name(),
                request.description(),
                request.price(),
                request.productType(),
                request.productStatus());

        productRepository.save(product);

        return ProductCreateResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductListResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }
}


