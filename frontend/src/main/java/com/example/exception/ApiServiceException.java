package com.example.exception;

/**
 * API 服務自定義異常
 */
public class ApiServiceException extends RuntimeException {
    private final Integer httpCode;

    public ApiServiceException(String message) {
        super(message);
        this.httpCode = null;
    }

    public ApiServiceException(String message, Throwable cause) {
        super(message, cause);
        this.httpCode = null;
    }

    public ApiServiceException(String message, Integer httpCode, Throwable cause) {
        super(message, cause);
        this.httpCode = httpCode;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}