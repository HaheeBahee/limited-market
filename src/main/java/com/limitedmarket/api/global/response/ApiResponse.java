package com.limitedmarket.api.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    // 데이터 있는 성공
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(null, message, data);
    }

    // 데이터 없는 성공
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(null, message, null);
    }

    // 에러 (data 없음)
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // 에러 (data 있음 - Validation 에러 목록 등)
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
