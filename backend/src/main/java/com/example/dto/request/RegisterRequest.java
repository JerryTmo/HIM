package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用戶註冊請求")
public class RegisterRequest extends LoginRequest {

    @NotBlank(message = "郵箱不能為空")
    @Email(message = "郵箱格式不正確")
    @Schema(description = "郵箱", requiredMode = RequiredMode.REQUIRED, example = "user@example.com")
    private String email;
}
