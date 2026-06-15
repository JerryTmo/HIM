package com.example.exception;

import com.example.enums.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode code;
    private final Object data;

    public BusinessException(ResultCode code) {
        super(code.getMessage());
        this.code = code;
        this.data = null;
    }

    public BusinessException(ResultCode code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }

    public BusinessException(ResultCode code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BusinessException(ResultCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
        this.data = null;
    }
}
