package com.example.dto.request;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "登錄請求參數")
public class LoginRequest {

    @NotBlank(message = "用戶名不能為空")
    @Size(min = 4, max = 20, message = "用戶名長度必須在4-20個字符之間")
    @Schema(description = "用戶名", requiredMode = RequiredMode.REQUIRED, example = "admin")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 20, message = "密碼長度必須在6-20個字符之間")
    @Schema(description = "密碼", requiredMode = RequiredMode.REQUIRED, example = "123456")
    private String password;
}
