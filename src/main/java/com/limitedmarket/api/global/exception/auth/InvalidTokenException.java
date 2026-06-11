package com.limitedmarket.api.global.exception.auth;

import com.limitedmarket.api.global.exception.CustomException;
import com.limitedmarket.api.global.exception.ErrorCode;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
