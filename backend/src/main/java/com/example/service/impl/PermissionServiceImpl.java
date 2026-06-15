package com.example.service.impl;

import com.example.entity.PermissionEntity;
import com.example.entity.RoleEntity;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.service.PermissionService;
import com.example.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final UserRepository userRepository;
    private final UserUtils userUtils;

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String permissionCode) {
        Set<String> permissions = getCurrentUserPermissions();
        return permissions.contains(permissionCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        Set<String> userPermissions = getCurrentUserPermissions();
        for (String permissionCode : permissionCodes) {
            if (userPermissions.contains(permissionCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) {
            return true;
        }

        Set<String> userPermissions = getCurrentUserPermissions();
        for (String permissionCode : permissionCodes) {
            if (!userPermissions.contains(permissionCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(String roleCode) {
        Set<String> roles = getCurrentUserRoles();
        return roles.contains(roleCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyRole(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return true;
        }

        Set<String> userRoles = getCurrentUserRoles();
        for (String roleCode : roleCodes) {
            if (userRoles.contains(roleCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllRoles(String... roleCodes) {
        if (roleCodes == null || roleCodes.length == 0) {
            return true;
        }

        Set<String> userRoles = getCurrentUserRoles();
        for (String roleCode : roleCodes) {
            if (!userRoles.contains(roleCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getCurrentUserPermissions() {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return Collections.emptySet();
        }

        return user.getRoles().stream()
                .filter(Objects::nonNull)
                .map(RoleEntity::getPermissions)
                .flatMap(Set::stream)
                .filter(Objects::nonNull)
                .map(PermissionEntity::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getCurrentUserRoles() {
        UserEntity user = getCurrentUser();
        if (user == null) {
            return Collections.emptySet();
        }

        return user.getRoles().stream()
                .filter(Objects::nonNull)
                .map(RoleEntity::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public String getCurrentUserId() {
        UserEntity user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    private UserEntity getCurrentUser() {
        try {
            String username = getCurrentUsername();
            if (username == null) {
                return null;
            }
            return userRepository.findByUsernameWithRoles(username).orElse(null);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return null;
        }
    }
}
