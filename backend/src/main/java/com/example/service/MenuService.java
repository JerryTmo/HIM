package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
import com.example.dto.response.MenuResponse.MenuDTO;

import java.util.List;

public interface MenuService {

    /**
     * 获取当前用户的菜单树（按角色权限过滤）
     */
    ServiceResult<List<MenuDTO>> findByMenu(String username);

    /**
     * 获取所有菜单（树形结构，用于管理端）
     */
    ServiceResult<List<MenuDTO>> findAllMenus();

    /**
     * 获取单个菜单详情
     */
    ServiceResult<MenuDTO> getMenuById(String id);

    /**
     * 创建菜单
     */
    ServiceResult<String> createMenu(MenuRequest request);

    /**
     * 更新菜单
     */
    ServiceResult<Void> updateMenu(String id, MenuRequest request);

    /**
     * 删除菜单
     */
    ServiceResult<Void> deleteMenu(String id);
}
