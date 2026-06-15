package com.example.service.impl;

import com.example.common.ServiceResult;
import com.example.dto.request.RoleRequest;
import com.example.dto.response.RoleResponse;
import com.example.entity.PermissionEntity;
import com.example.entity.RoleEntity;
import com.example.entity.UserEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createRole(RoleRequest.CreateRoleRequest request) {
        if (roleRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ResultCode.ROLE_EXISTS);
        }

        RoleEntity role = RoleEntity.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        role = roleRepository.save(role);
        log.info("创建角色成功: {}", role.getCode());
        return ServiceResult.success(role.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateRole(RoleRequest.UpdateRoleRequest request) {
        RoleEntity role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.ROLE_NOT_FOUND));

        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getIsDefault() != null) {
            role.setIsDefault(request.getIsDefault());
        }

        roleRepository.save(role);
        log.info("更新角色成功: {}", role.getCode());
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteRole(String roleId) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ResultCode.ROLE_NOT_FOUND));

        if (role.getIsDefault()) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_SYSTEM_ROLE);
        }

        roleRepository.delete(role);
        log.info("删除角色成功: {}", role.getCode());
        return ServiceResult.success();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<RoleResponse.RoleDetailResponse> getRoleById(String roleId) {
        RoleEntity role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new BusinessException(ResultCode.ROLE_NOT_FOUND));

        RoleResponse.RoleDetailResponse response = new RoleResponse.RoleDetailResponse();
        BeanUtils.copyProperties(role, response);

        List<RoleResponse.PermissionInfoResponse> permissions = role.getPermissions().stream()
                .map(p -> {
                    RoleResponse.PermissionInfoResponse pRes = new RoleResponse.PermissionInfoResponse();
                    BeanUtils.copyProperties(p, pRes);
                    return pRes;
                })
                .collect(Collectors.toList());
        response.setPermissions(permissions);

        return ServiceResult.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<RoleResponse.RoleInfoResponse>> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        List<RoleResponse.RoleInfoResponse> responses = roles.stream()
                .map(role -> {
                    RoleResponse.RoleInfoResponse res = new RoleResponse.RoleInfoResponse();
                    BeanUtils.copyProperties(role, res);
                    return res;
                })
                .collect(Collectors.toList());
        return ServiceResult.success(responses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> assignPermissionsToRole(String roleId, List<String> permissionIds) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ResultCode.ROLE_NOT_FOUND));

        Set<PermissionEntity> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(permissions);
        roleRepository.save(role);

        log.info("为角色 {} 分配权限成功，权限数量: {}", role.getCode(), permissions.size());
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> assignRolesToUser(String userId, List<String> roleIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        Set<RoleEntity> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);

        log.info("为用户 {} 分配角色成功，角色数量: {}", user.getUsername(), roles.size());
        return ServiceResult.success();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<RoleResponse.RoleInfoResponse>> getUserRoles(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        List<RoleResponse.RoleInfoResponse> responses = user.getRoles().stream()
                .map(role -> {
                    RoleResponse.RoleInfoResponse res = new RoleResponse.RoleInfoResponse();
                    BeanUtils.copyProperties(role, res);
                    return res;
                })
                .collect(Collectors.toList());
        return ServiceResult.success(responses);
    }
}
