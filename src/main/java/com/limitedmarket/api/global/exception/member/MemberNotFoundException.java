package com.limitedmarket.api.global.exception.member;

import com.limitedmarket.api.global.exception.CustomException;
import com.limitedmarket.api.global.exception.ErrorCode;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}