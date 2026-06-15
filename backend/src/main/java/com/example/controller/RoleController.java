package com.example.controller;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.RoleRequest;
import com.example.dto.response.RoleResponse;
import com.example.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "角色管理")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "创建角色")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"role:create"})
    public ServiceResult<String> createRole(@Valid @RequestBody RoleRequest.CreateRoleRequest request) {
        return roleService.createRole(request);
    }

    @PutMapping
    @Operation(summary = "更新角色")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"role:update"})
    public ServiceResult<Void> updateRole(@Valid @RequestBody RoleRequest.UpdateRoleRequest request) {
        return roleService.updateRole(request);
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"role:delete"})
    public ServiceResult<Void> deleteRole(@PathVariable String roleId) {
        return roleService.deleteRole(roleId);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"role:read"})
    public ServiceResult<RoleResponse.RoleDetailResponse> getRoleById(@PathVariable String roleId) {
        return roleService.getRoleById(roleId);
    }

    @GetMapping
    @Operation(summary = "获取所有角色")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"role:read"})
    public ServiceResult<List<RoleResponse.RoleInfoResponse>> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "为角色分配权限")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"role:assign"})
    public ServiceResult<Void> assignPermissionsToRole(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        return roleService.assignPermissionsToRole(roleId, permissionIds);
    }

    @PostMapping("/assign/{userId}")
    @Operation(summary = "为用户分配角色")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"user:role:assign"})
    public ServiceResult<Void> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return roleService.assignRolesToUser(userId, roleIds);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的角色列表")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
    @RequirePermission({"user:role:read"})
    public ServiceResult<List<RoleResponse.RoleInfoResponse>> getUserRoles(@PathVariable String userId) {
        return roleService.getUserRoles(userId);
    }
}
