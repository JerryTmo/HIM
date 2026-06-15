package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest;
import com.example.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionManagementService {
    /**
     * 创建权限
     */
    ServiceResult<String> createPermission(PermissionRequest.CreatePermissionRequest request);

    /**
     * 更新权限
     */
    ServiceResult<Void> updatePermission(PermissionRequest.UpdatePermissionRequest request);

    /**
     * 删除权限
     */
    ServiceResult<Void> deletePermission(String permissionId);

    /**
     * 获取权限详情
     */
    ServiceResult<PermissionResponse.PermissionDetailResponse> getPermissionById(String permissionId);

    /**
     * 获取所有权限列表
     */
    ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getAllPermissions();

    /**
     * 按模块获取权限
     */
    ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getPermissionsByModule(String module);

    /**
     * 获取所有模块列表
     */
    ServiceResult<List<String>> getAllModules();
}
