package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "插入菜單請求參數")
public class MenuRequest {

    @NotBlank(message = "標題不能為空")
    @Schema(description = "菜單標題", requiredMode = RequiredMode.REQUIRED, example = "用戶管理")
    private String title;

    @NotBlank(message = "路由不能為空")
    @Schema(description = "菜單路由路徑", requiredMode = RequiredMode.REQUIRED, example = "/system/user")
    private String route;

    @Schema(description = "菜單圖標", example = "user")
    private String icon;

    @NotNull(message = "排序不能為空")
    @Schema(description = "排序順序", requiredMode = RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;

    @Schema(description = "是否啟用", example = "true")
    private Boolean isActive = true;

    @NotBlank(message = "模組不能為空")
    @Schema(description = "所屬模組", requiredMode = RequiredMode.REQUIRED, example = "system")
    private String module;

    @Schema(description = "父菜單ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String parentId; // 你可能還需要這個字段
}