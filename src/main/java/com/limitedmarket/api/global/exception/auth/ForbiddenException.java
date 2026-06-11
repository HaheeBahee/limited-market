package com.limitedmarket.api.global.exception.auth;

import com.limitedmarket.api.global.exception.CustomException;
import com.limitedmarket.api.global.exception.ErrorCode;

public class ForbiddenException extends CustomException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}