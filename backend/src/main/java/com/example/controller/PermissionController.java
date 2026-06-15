package com.example.controller;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest;
import com.example.dto.response.PermissionResponse;
import com.example.service.PermissionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "权限管理")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionManagementService permissionManagementService;

    @PostMapping
    @Operation(summary = "创建权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"permission:create"})
    public ServiceResult<String> createPermission(@Valid @RequestBody PermissionRequest.CreatePermissionRequest request) {
        return permissionManagementService.createPermission(request);
    }

    @PutMapping
    @Operation(summary = "更新权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"permission:update"})
    public ServiceResult<Void> updatePermission(@Valid @RequestBody PermissionRequest.UpdatePermissionRequest request) {
        return permissionManagementService.updatePermission(request);
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "删除权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"permission:delete"})
    public ServiceResult<Void> deletePermission(@PathVariable String permissionId) {
        return permissionManagementService.deletePermission(permissionId);
    }

    @GetMapping("/{permissionId}")
    @Operation(summary = "获取权限详情")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"permission:read"})
    public ServiceResult<PermissionResponse.PermissionDetailResponse> getPermissionById(@PathVariable String permissionId) {
        return permissionManagementService.getPermissionById(permissionId);
    }

    @GetMapping
    @Operation(summary = "获取所有权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"permission:read"})
    public ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getAllPermissions() {
        return permissionManagementService.getAllPermissions();
    }

    @GetMapping("/module/{module}")
    @Operation(summary = "按模块获取权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"permission:read"})
    public ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getPermissionsByModule(@PathVariable String module) {
        return permissionManagementService.getPermissionsByModule(module);
    }

    @GetMapping("/modules")
    @Operation(summary = "获取所有模块列表")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"permission:read"})
    public ServiceResult<List<String>> getAllModules() {
        return permissionManagementService.getAllModules();
    }
}
