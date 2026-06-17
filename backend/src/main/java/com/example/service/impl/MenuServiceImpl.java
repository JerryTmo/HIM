package com.example.service.impl;

import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
import com.example.dto.response.MenuResponse.MenuDTO;
import com.example.entity.MenuEntity;
import com.example.entity.PermissionEntity;
import com.example.entity.RoleEntity;
import com.example.entity.UserEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.MenuRepository;
import com.example.repository.PermissionRepository;
import com.example.repository.UserRepository;
import com.example.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;

    // ==================== 查询 ====================

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<MenuDTO>> findByMenu(String username) {
        UserEntity user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        Set<MenuEntity> accessible = getAccessibleMenus(user);
        List<MenuDTO> tree = accessible.stream()
                .filter(m -> m.getParent() == null)
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(m -> toTreeDTO(m, accessible))
                .collect(Collectors.toList());
        return ServiceResult.success(tree);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<MenuDTO>> findAllMenus() {
        List<MenuEntity> all = menuRepository.findAllByOrderBySortOrderAsc();
        Set<String> allIds = all.stream().map(MenuEntity::getId).collect(Collectors.toSet());
        List<MenuDTO> tree = all.stream()
                .filter(m -> m.getParent() == null)
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(m -> toFullDTO(m, allIds))
                .collect(Collectors.toList());
        return ServiceResult.success(tree);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<MenuDTO> getMenuById(String id) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        return ServiceResult.success(toDTO(menu));
    }

    // ==================== 写入 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createMenu(MenuRequest req) {
        if (menuRepository.existsByRoute(req.getRoute())) {
            return ServiceResult.error("路由已存在: " + req.getRoute());
        }

        MenuEntity menu = MenuEntity.builder()
                .title(req.getTitle())
                .route(req.getRoute())
                .icon(req.getIcon())
                .sortOrder(req.getSortOrder())
                .isActive(req.getIsActive())
                .module(req.getModule())
                .build();

        // 关联父菜单
        if (req.getParentId() != null) {
            MenuEntity parent = menuRepository.findById(req.getParentId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            menu.setParent(parent);
        }

        menuRepository.save(menu);

        // 自动关联模块权限
        associateModulePermissions(menu);
        return ServiceResult.success(menu.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateMenu(String id, MenuRequest req) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));

        menu.setTitle(req.getTitle());
        menu.setRoute(req.getRoute());
        menu.setIcon(req.getIcon());
        menu.setSortOrder(req.getSortOrder());
        menu.setIsActive(req.getIsActive());
        menu.setModule(req.getModule());

        // 更新父菜单关联
        if (req.getParentId() != null) {
            MenuEntity parent = menuRepository.findById(req.getParentId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }

        menuRepository.save(menu);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteMenu(String id) {
        MenuEntity menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));

        // 解除权限关联
        for (PermissionEntity perm : menu.getPermissions()) {
            perm.getMenus().remove(menu);
        }
        menu.getPermissions().clear();

        // 解除子菜单关联
        for (MenuEntity child : menu.getChildren()) {
            child.setParent(null);
        }
        menu.getChildren().clear();

        menuRepository.delete(menu);
        return ServiceResult.success();
    }

    // ==================== 私有方法 ====================

    /**
     * 获取用户有权限访问的所有菜单
     */
    private Set<MenuEntity> getAccessibleMenus(UserEntity user) {
        return user.getRoles().stream()
                .filter(Objects::nonNull)
                .map(RoleEntity::getPermissions)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .filter(Objects::nonNull)
                .map(PermissionEntity::getMenus)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .filter(Objects::nonNull)
                .filter(MenuEntity::getIsActive)
                .collect(Collectors.toSet());
    }

    /**
     * 构建菜单树 DTO（按权限过滤子菜单）
     */
    private MenuDTO toTreeDTO(MenuEntity menu, Set<MenuEntity> accessible) {
        List<MenuDTO> children = menu.getChildren().stream()
                .filter(accessible::contains)
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(child -> toTreeDTO(child, accessible))
                .collect(Collectors.toList());

        return MenuDTO.builder()
                .id(menu.getId())
                .title(menu.getTitle())
                .route(menu.getRoute())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .isActive(menu.getIsActive())
                .module(menu.getModule())
                .children(children.isEmpty() ? null : children)
                .build();
    }

    /**
     * 构建完整 DTO（不过滤）
     */
    private MenuDTO toFullDTO(MenuEntity menu, Set<String> allIds) {
        List<MenuDTO> children = menu.getChildren().stream()
                .filter(c -> allIds.contains(c.getId()))
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(child -> toFullDTO(child, allIds))
                .collect(Collectors.toList());

        MenuDTO dto = toDTO(menu);
        dto.setChildren(children.isEmpty() ? null : children);
        return dto;
    }

    /**
     * Entity → DTO 基础转换
     */
    private MenuDTO toDTO(MenuEntity menu) {
        return MenuDTO.builder()
                .id(menu.getId())
                .title(menu.getTitle())
                .route(menu.getRoute())
                .icon(menu.getIcon())
                .sortOrder(menu.getSortOrder())
                .isActive(menu.getIsActive())
                .module(menu.getModule())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .parentTitle(menu.getParent() != null ? menu.getParent().getTitle() : null)
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    /**
     * 将菜单关联到所属模块的权限
     */
    private void associateModulePermissions(MenuEntity menu) {
        if (menu.getModule() == null) return;

        List<PermissionEntity> perms = permissionRepository.findByModule(menu.getModule());
        for (PermissionEntity perm : perms) {
            menu.getPermissions().add(perm);
            perm.getMenus().add(menu);
        }
        menuRepository.save(menu);
    }
}
