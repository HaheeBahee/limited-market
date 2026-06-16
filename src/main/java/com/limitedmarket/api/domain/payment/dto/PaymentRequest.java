package com.limitedmarket.api.domain.payment.dto;

import com.limitedmarket.api.domain.delivery.dto.DeliveryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @Schema(example = "imp_mock_20260616124417")
        @NotBlank String impUid,
        @Schema(example = "30000")
        @NotNull BigDecimal amount,
        @Valid @NotNull DeliveryRequest deliveryRequest
) {
}
