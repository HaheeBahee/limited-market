package com.bookstore.api.domain.product;

import com.bookstore.api.domain.product.dto.ProductCreateRequest;
import com.bookstore.api.domain.product.dto.ProductCreateResponse;
import com.bookstore.api.domain.product.dto.ProductDetailResponse;
import com.bookstore.api.domain.product.dto.ProductListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "2. 상품")
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록")
    public ResponseEntity<ProductCreateResponse> register(@RequestBody @Valid ProductCreateRequest request) {
        ProductCreateResponse response = productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회")
    public ResponseEntity<List<ProductListResponse>> getProducts() {
        List<ProductListResponse> response = productService.getProducts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long id) {
        ProductDetailResponse response = productService.getProduct(id);
        return ResponseEntity.ok(response);

    }
}
