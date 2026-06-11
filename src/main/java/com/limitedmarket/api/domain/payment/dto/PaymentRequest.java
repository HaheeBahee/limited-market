package com.limitedmarket.api.domain.payment.dto;

import com.limitedmarket.api.domain.delivery.dto.DeliveryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String impUid,
        @NotNull BigDecimal amount,
        @Valid @NotNull DeliveryRequest deliveryRequest
) {
}
