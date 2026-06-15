package com.example.menu;

/**
 * 統一的結果碼
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),

    // 客戶端錯誤 (4xx)
    BAD_REQUEST(400, "請求參數錯誤"),
    UNAUTHORIZED(401, "未授權"),
    FORBIDDEN(403, "禁止訪問"),
    NOT_FOUND(404, "資源不存在"),

    // 服務器錯誤 (5xx)
    ERROR(500, "系統錯誤"),
    SERVICE_UNAVAILABLE(503, "服務不可用"),

    // 業務錯誤 (自定義)
    // 登錄相關
    EMPTY_USERNAME(1001, "用戶名不能為空"),
    EMPTY_PASSWORD(1002, "密碼不能為空"),
    USER_NOT_FOUND(1003, "用戶不存在"),
    WRONG_PASSWORD(1004, "密碼錯誤"),
    USER_LOCKED(1005, "賬戶已鎖定"),
    USER_DISABLED(1006, "賬戶已禁用"),
    CAPTCHA_ERROR(1007, "驗證碼錯誤"),

    // 註冊相關
    USERNAME_EXISTS(1101, "用戶名已存在"),
    EMAIL_EXISTS(1102, "郵箱已被註冊"),
    PASSWORD_WEAK(1103, "密碼強度不足"),

    // 權限相關
    NO_PERMISSION(1201, "沒有權限"),

    // 數據相關
    DATA_NOT_FOUND(1301, "數據不存在"),
    DATA_EXISTS(1302, "數據已存在"),
    DATA_INVALID(1303, "數據無效");

    private final int value;
    private final String defaultMessage;

    ResultCode(int value, String defaultMessage) {
        this.value = value;
        this.defaultMessage = defaultMessage;
    }

    public int getValue() {
        return value;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * 根據值獲取枚舉
     */
    public static ResultCode fromValue(int value) {
        for (ResultCode code : ResultCode.values()) {
            if (code.value == value) {
                return code;
            }
        }
        return ERROR;
    }
}