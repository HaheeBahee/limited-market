package com.bookstore.api.global.exception.auth;

import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;

public class TokenExpiredException extends CustomException {
    public TokenExpiredException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
}
