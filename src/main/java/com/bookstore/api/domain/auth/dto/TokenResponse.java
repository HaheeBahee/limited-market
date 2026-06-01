package com.bookstore.api.domain.auth.dto;

// Service 내부용 (accessToken + refreshToken) -> Controller가 받아서 처리
public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
