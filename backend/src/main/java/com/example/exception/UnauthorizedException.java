package com.example.exception;

import com.example.enums.ResultCode;

/**
 * 未授权异常
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }
}
