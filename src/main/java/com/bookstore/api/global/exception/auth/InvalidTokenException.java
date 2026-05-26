package com.bookstore.api.global.exception.auth;

import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
