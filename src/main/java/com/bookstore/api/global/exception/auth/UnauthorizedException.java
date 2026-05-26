package com.bookstore.api.global.exception.auth;

import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}