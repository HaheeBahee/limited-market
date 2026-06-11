package com.limitedmarket.api.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeliveryRequest(
        @NotBlank @Schema(example = "홍길동") String recipientName,
        @NotBlank @Schema(example = "010-1234-5678") String phone,
        @NotBlank @Schema(example = "서울시 강남구") String city,
        @NotBlank @Schema(example = "테헤란로 123") String street,
        @NotBlank @Schema(example = "12345") String zipcode
) {}
