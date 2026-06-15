package com.example.dto.request;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

public class PermissionRequest {

    @Data
    @Schema(description = "角色與權限參數")
    public static class UpdateCodeRequest {
        @NotBlank(message = "角色Id不能為空")
        @Schema(description = "角色ID", requiredMode = RequiredMode.REQUIRED, example = "123e4567-e89b-12d3-a456-426614174000")
        private String roleId;

        @NotEmpty(message = "權限Id不能為空")
        @Schema(description = "權限ID", requiredMode = RequiredMode.REQUIRED, example = "[\"perm1\", \"perm2\"]")
        private Set<String> permissionIds;
    }
}
