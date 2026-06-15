package com.example.enums;

/**
 * 狀態碼
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 (4xx)
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 服务端错误 (5xx)
    ERROR(500, "系统错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 认证相关 (1001-1099)
    EMPTY_USERNAME(1001, "用户名不能为空"),
    EMPTY_PASSWORD(1002, "密码不能为空"),
    USER_NOT_FOUND(1003, "用户不存在"),
    WRONG_PASSWORD(1004, "密码错误"),
    USER_LOCKED(1005, "账户已锁定"),
    TOKEN_EXPIRED(1006, "令牌已过期"),
    TOKEN_INVALID(1007, "无效令牌"),

    // 注册相关 (1101-1199)
    USERNAME_EXISTS(1101, "用户名已存在"),
    EMAIL_EXISTS(1102, "邮箱已被注册"),
    PASSWORD_WEAK(1103, "密码强度不足"),

    // 權限相關
    ROLE_NOT_FOUND(3001, "沒有當前角色"),

    // 相冊相關
    ALBUM_NAME_EXISTS(3010, "相冊已存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
