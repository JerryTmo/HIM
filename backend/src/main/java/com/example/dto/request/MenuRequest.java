package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "菜单请求参数")
public class MenuRequest {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "菜单标题", requiredMode = RequiredMode.REQUIRED, example = "用户管理")
    private String title;

    @Schema(description = "菜单路由路径（叶子节点必填）", example = "patient-management")
    private String route;

    @Schema(description = "菜单图标", example = "🏥")
    private String icon;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序顺序", requiredMode = RequiredMode.REQUIRED, example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用", example = "true")
    private Boolean isActive = true;

    @NotBlank(message = "模块不能为空")
    @Schema(description = "所属模块", requiredMode = RequiredMode.REQUIRED, example = "system")
    private String module;

    @Schema(description = "父菜单ID（顶级菜单不传）", example = "123e4567-e89b-12d3-a456-426614174000")
    private String parentId;
}
