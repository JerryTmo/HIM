package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

public class RoleRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建角色请求")
    public static class CreateRoleRequest {
        @NotBlank(message = "角色名称不能为空")
        @Schema(description = "角色名称", required = true, example = "管理员")
        private String name;

        @NotBlank(message = "角色编码不能为空")
        @Schema(description = "角色编码", required = true, example = "ROLE_ADMIN")
        private String code;

        @Schema(description = "角色描述", example = "系统管理员角色")
        private String description;

        @Schema(description = "是否默认角色", example = "false")
        private Boolean isDefault;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新角色请求")
    public static class UpdateRoleRequest {
        @NotBlank(message = "角色ID不能为空")
        @Schema(description = "角色ID", required = true)
        private String id;

        @Schema(description = "角色名称", example = "管理员")
        private String name;

        @Schema(description = "角色描述", example = "系统管理员角色")
        private String description;

        @Schema(description = "是否默认角色", example = "false")
        private Boolean isDefault;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "为角色分配权限请求")
    public static class AssignPermissionsRequest {
        @NotBlank(message = "角色ID不能为空")
        @Schema(description = "角色ID", required = true)
        private String roleId;

        @Schema(description = "权限ID列表")
        private List<String> permissionIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "为用户分配角色请求")
    public static class AssignRolesToUserRequest {
        @NotBlank(message = "用户ID不能为空")
        @Schema(description = "用户ID", required = true)
        private String userId;

        @Schema(description = "角色ID列表")
        private List<String> roleIds;
    }
}
