package com.example.common;

import com.example.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "統一響應結果")
public class ServiceResult<T> {

    @Schema(description = "是否成功", example = "true")
    private boolean success;

    @Schema(description = "提示信息", example = "成功")
    private String message;

    @Schema(description = "返回數據")
    private T data;

    @Schema(description = "狀態碼", example = "200")
    private Integer code;

    @Schema(description = "時間戳")
    private Long timestamp;

    private ServiceResult() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功 - 只有数据
     */
    public static <T> ServiceResult<T> success(T data) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 成功 - 带消息
     */
    public static <T> ServiceResult<T> success(T data, String message) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败 - 默认错误
     */
    public static <T> ServiceResult<T> error(String message) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败 - 带错误码
     */
    public static <T> ServiceResult<T> error(String message, ResultCode code) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(false);
        result.setCode(code.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败 - 带数据
     */
    public static <T> ServiceResult<T> error(String message, T data) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败 - 带错误码和数据
     */
    public static <T> ServiceResult<T> error(String message, ResultCode code, T data) {
        ServiceResult<T> result = new ServiceResult<>();
        result.setSuccess(false);
        result.setCode(code.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 判斷是否成功
     * 注意：Lombok 的 @Data 會生成 getSuccess()，但不會生成 isSuccess()
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 判斷是否失敗
     */
    public boolean isError() {
        return !success;
    }
}