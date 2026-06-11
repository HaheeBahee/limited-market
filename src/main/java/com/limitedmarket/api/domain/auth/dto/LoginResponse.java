package com.limitedmarket.api.domain.auth.dto;

// 클라이언트 응답용 (accessToken만 포함)
public record LoginResponse(
        String accessToken
) {
}
