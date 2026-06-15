package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.common.ServiceResult;
import com.example.enums.ResultCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ServiceResult<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ServiceResult.error(e.getMessage(), e.getCode(), e.getData());
    }

    /**
     * 处理认证异常 - 用户名不存在
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ServiceResult<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("认证异常: {}", e.getMessage());
        return ServiceResult.error(e.getMessage(), ResultCode.USER_NOT_FOUND);
    }

    /**
     * 处理认证异常 - 密码错误
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ServiceResult<?> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("认证异常: 密码错误");
        return ServiceResult.error("用户名或密码错误", ResultCode.WRONG_PASSWORD);
    }

    /**
     * 处理授权异常 - 权限不足
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ServiceResult<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("授权异常: {}", e.getMessage());
        return ServiceResult.error("没有权限访问", ResultCode.FORBIDDEN);
    }

    /**
     * 处理令牌异常
     */
    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ServiceResult<?> handleTokenException(TokenException e) {
        log.warn("令牌异常: {}", e.getMessage());
        return ServiceResult.error(e.getMessage(), e.getCode());
    }

    /**
     * 处理参数校验异常 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceResult<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("参数校验失败: {}", errors);
        return ServiceResult.error("请求参数错误", ResultCode.BAD_REQUEST, errors);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceResult<Map<String, String>> handleBindException(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("参数绑定失败: {}", errors);
        return ServiceResult.error("请求参数错误", ResultCode.BAD_REQUEST, errors);
    }

    /**
     * 处理参数类型转换异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServiceResult<?> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String error = String.format("参数 '%s' 类型错误，应该为 %s",
                e.getName(), e.getRequiredType().getSimpleName());
        log.warn("参数类型错误: {}", error);
        return ServiceResult.error(error, ResultCode.BAD_REQUEST);
    }

    /**
     * 处理所有其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServiceResult<?> handleAllUncaughtException(Exception e) {
        log.error("系统异常:", e);
        return ServiceResult.error("系统繁忙，请稍后重试", ResultCode.ERROR);
    }
}
