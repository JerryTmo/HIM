package com.example.service;

import java.util.Set;

/**
 * 权限验证服务接口
 */
public interface PermissionService {
    /**
     * 检查当前用户是否有指定权限
     * @param permissionCode 权限编码
     * @return 是否拥有权限
     */
    boolean hasPermission(String permissionCode);

    /**
     * 检查当前用户是否有指定权限（多个权限任一即可）
     * @param permissionCodes 权限编码数组
     * @return 是否拥有任一权限
     */
    boolean hasAnyPermission(String... permissionCodes);

    /**
     * 检查当前用户是否拥有所有指定权限
     * @param permissionCodes 权限编码数组
     * @return 是否拥有所有权限
     */
    boolean hasAllPermissions(String... permissionCodes);

    /**
     * 检查当前用户是否有指定角色
     * @param roleCode 角色编码
     * @return 是否拥有角色
     */
    boolean hasRole(String roleCode);

    /**
     * 检查当前用户是否有指定角色（多个角色任一即可）
     * @param roleCodes 角色编码数组
     * @return 是否拥有任一角色
     */
    boolean hasAnyRole(String... roleCodes);

    /**
     * 检查当前用户是否拥有所有指定角色
     * @param roleCodes 角色编码数组
     * @return 是否拥有所有角色
     */
    boolean hasAllRoles(String... roleCodes);

    /**
     * 获取当前用户的所有权限编码
     * @return 权限编码集合
     */
    Set<String> getCurrentUserPermissions();

    /**
     * 获取当前用户的所有角色编码
     * @return 角色编码集合
     */
    Set<String> getCurrentUserRoles();

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    String getCurrentUserId();

    /**
     * 获取当前用户名
     * @return 用户名
     */
    String getCurrentUsername();
}
