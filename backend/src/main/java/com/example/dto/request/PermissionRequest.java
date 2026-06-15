package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class PermissionRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建权限请求")
    public static class CreatePermissionRequest {
        @NotBlank(message = "权限名称不能为空")
        @Schema(description = "权限名称", required = true, example = "创建用户")
        private String name;

        @NotBlank(message = "权限编码不能为空")
        @Schema(description = "权限编码", required = true, example = "user:create")
        private String code;

        @NotBlank(message = "模块名称不能为空")
        @Schema(description = "模块", required = true, example = "用户管理")
        private String module;

        @Schema(description = "权限描述", example = "创建新用户的权限")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新权限请求")
    public static class UpdatePermissionRequest {
        @NotBlank(message = "权限ID不能为空")
        @Schema(description = "权限ID", required = true)
        private String id;

        @Schema(description = "权限名称", example = "创建用户")
        private String name;

        @Schema(description = "模块", example = "用户管理")
        private String module;

        @Schema(description = "权限描述", example = "创建新用户的权限")
        private String description;
    }
}
