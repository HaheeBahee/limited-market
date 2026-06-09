package com.bookstore.api.domain.order;

import com.bookstore.api.domain.order.dto.OrderCreateRequest;
import com.bookstore.api.domain.order.dto.OrderCreateResponse;
import com.bookstore.api.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. 주문")
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> create(@RequestBody @Valid OrderCreateRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        OrderCreateResponse response = orderService.create(request, userDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{orderId}/cancel/")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId, @AuthenticationPrincipal CustomUserDetails userDetails){
        orderService.cancel(orderId, userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}
