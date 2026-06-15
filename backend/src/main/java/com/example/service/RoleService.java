package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.RoleRequest;
import com.example.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    /**
     * 创建角色
     */
    ServiceResult<String> createRole(RoleRequest.CreateRoleRequest request);

    /**
     * 更新角色
     */
    ServiceResult<Void> updateRole(RoleRequest.UpdateRoleRequest request);

    /**
     * 删除角色
     */
    ServiceResult<Void> deleteRole(String roleId);

    /**
     * 获取角色详情
     */
    ServiceResult<RoleResponse.RoleDetailResponse> getRoleById(String roleId);

    /**
     * 获取所有角色列表
     */
    ServiceResult<List<RoleResponse.RoleInfoResponse>> getAllRoles();

    /**
     * 为角色分配权限
     */
    ServiceResult<Void> assignPermissionsToRole(String roleId, List<String> permissionIds);

    /**
     * 为用户分配角色
     */
    ServiceResult<Void> assignRolesToUser(String userId, List<String> roleIds);

    /**
     * 获取用户的角色列表
     */
    ServiceResult<List<RoleResponse.RoleInfoResponse>> getUserRoles(String userId);
}
