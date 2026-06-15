package com.example.exception;

import com.example.enums.ResultCode;

/**
 * 令牌异常
 */
public class TokenException extends BusinessException {
    public TokenException(ResultCode code) {
        super(code);
    }

    public TokenException(ResultCode code, String message) {
        super(code, message);
    }
}