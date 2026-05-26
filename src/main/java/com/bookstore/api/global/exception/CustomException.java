package com.bookstore.api.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage()); // 로그에서 메시지 출력용
        this.errorCode = errorCode; // ErrorCode 저장
    }
}
