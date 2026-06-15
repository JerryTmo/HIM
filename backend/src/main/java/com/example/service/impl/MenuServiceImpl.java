package com.example.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
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
import com.example.service.PermissionService;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PermissionService permissionService;

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<MenuDTO>> findByMenu(String username) {
        UserEntity userEntity = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
        log.info("用戶信息:{}", userEntity);

        Set<MenuEntity> accessMenus = getAccessibleMenus(userEntity);

        List<MenuDTO> menuDTOs = accessMenus.stream()
                .filter(menu -> menu.getParent() == null)
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(menu -> convertToDTO(menu, accessMenus))
                .collect(Collectors.toList());
        return ServiceResult.success(menuDTOs);
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'findByMenu'");
    }

    private Set<MenuEntity> getAccessibleMenus(UserEntity userEntity) {
        return userEntity.getRoles().stream()
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

    private MenuDTO convertToDTO(MenuEntity menu, Set<MenuEntity> allAccessibleMenus) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setTitle(menu.getTitle());
        dto.setRoute(menu.getRoute());
        dto.setIcon(menu.getIcon());
        dto.setSortOrder(menu.getSortOrder());
        dto.setModule(menu.getModule());

        List<MenuDTO> children = menu.getChildren().stream()
                .filter(allAccessibleMenus::contains)
                .sorted(Comparator.comparing(MenuEntity::getSortOrder))
                .map(child -> convertToDTO(child, allAccessibleMenus))
                .collect(Collectors.toList());

        dto.setChildren(children);
        return dto;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuDTO {
        private String id;
        private String title;
        private String route;
        private String icon;
        private Integer sortOrder;
        private String module;
        private List<MenuDTO> children;
    }

    @Override
    public ServiceResult<Integer> insertMenus(MenuRequest menuRequest) {
        // 檢測是否存在數據
        if (menuRequest == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        // 檢查唯一性
        checkMenuUniqueness(menuRequest);

        // 創建菜單
        MenuEntity savedMenu = createMenu(menuRequest);

        // 處理權限關聯
        associatePermissions(savedMenu, menuRequest.getModule());

        return ServiceResult.success(null);
    }

    private void associatePermissions(MenuEntity savedMenu, String module) {
        if (StringUtils.isBlank(module)) {
            return;
        }
        Set<PermissionEntity> permissions = permissionService.getOrCreatePermissions(module);

        permissions.forEach(permission -> {
            savedMenu.getPermissions().add(permission);
            permission.getMenus().add(savedMenu);
        });

        menuRepository.save(savedMenu);
    }

    private MenuEntity createMenu(MenuRequest menuRequest) {
        // 構建菜單實體
        MenuEntity.MenuEntityBuilder builder = MenuEntity.builder()
                .title(menuRequest.getTitle())
                .route(menuRequest.getRoute())
                .icon(menuRequest.getIcon())
                .sortOrder(menuRequest.getSortOrder())
                .isActive(menuRequest.getIsActive())
                .module(menuRequest.getModule());
        // 查詢父菜單
        if (menuRequest.getParentId() != null && !menuRequest.getParentId().trim().isEmpty()) {
            MenuEntity parentMenu = menuRepository.findById(menuRequest.getParentId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
            builder.parent(parentMenu);
        }
        // 保存
        MenuEntity menuEntity = builder.build();
        return menuRepository.save(menuEntity);
    }

    private void checkMenuUniqueness(MenuRequest menuRequest) {
        if (menuRepository.existsByRoute(menuRequest.getRoute())) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
    }

}
