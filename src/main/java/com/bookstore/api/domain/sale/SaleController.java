package com.bookstore.api.domain.sale;

import com.bookstore.api.domain.sale.dto.SaleCreateRequest;
import com.bookstore.api.domain.sale.dto.SaleCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "3. 판매")
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @Operation(summary = "판매 등록", description = "상품 ID, 판매 가격, 재고 수량, VIP/일반 오픈 시간을 설정합니다")
    public ResponseEntity<SaleCreateResponse> create(@RequestBody @Valid SaleCreateRequest request){
        SaleCreateResponse response = saleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
