package com.limitedmarket.api.global.exception.member;

import com.limitedmarket.api.global.exception.CustomException;
import com.limitedmarket.api.global.exception.ErrorCode;

public class DuplicateEmailException extends CustomException {
    public DuplicateEmailException(){
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
