package com.bookstore.api.global.exception.auth;

import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;

public class ForbiddenException extends CustomException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}