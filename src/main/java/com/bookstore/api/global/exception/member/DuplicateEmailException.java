package com.bookstore.api.global.exception.member;

import com.bookstore.api.global.exception.CustomException;
import com.bookstore.api.global.exception.ErrorCode;

public class DuplicateEmailException extends CustomException {
    public DuplicateEmailException(){
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
