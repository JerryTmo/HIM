package com.example.service.impl;

import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest;
import com.example.dto.response.PermissionResponse;
import com.example.entity.PermissionEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.PermissionRepository;
import com.example.service.PermissionManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionManagementServiceImpl implements PermissionManagementService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createPermission(PermissionRequest.CreatePermissionRequest request) {
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ResultCode.PERMISSION_EXISTS);
        }

        PermissionEntity permission = PermissionEntity.builder()
                .name(request.getName())
                .code(request.getCode())
                .module(request.getModule())
                .description(request.getDescription())
                .build();

        permission = permissionRepository.save(permission);
        log.info("创建权限成功: {}", permission.getCode());
        return ServiceResult.success(permission.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updatePermission(PermissionRequest.UpdatePermissionRequest request) {
        PermissionEntity permission = permissionRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException(ResultCode.PERMISSION_EXISTS));

        if (request.getName() != null) {
            permission.setName(request.getName());
        }
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
        if (request.getModule() != null) {
            permission.setModule(request.getModule());
        }

        permissionRepository.save(permission);
        log.info("更新权限成功: {}", permission.getCode());
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deletePermission(String permissionId) {
        PermissionEntity permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(ResultCode.PERMISSION_EXISTS));

        permissionRepository.delete(permission);
        log.info("删除权限成功: {}", permission.getCode());
        return ServiceResult.success();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<PermissionResponse.PermissionDetailResponse> getPermissionById(String permissionId) {
        PermissionEntity permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(ResultCode.PERMISSION_EXISTS));

        PermissionResponse.PermissionDetailResponse response = new PermissionResponse.PermissionDetailResponse();
        BeanUtils.copyProperties(permission, response);
        return ServiceResult.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getAllPermissions() {
        List<PermissionEntity> permissions = permissionRepository.findAll();
        List<PermissionResponse.PermissionInfoResponse> responses = permissions.stream()
                .map(p -> {
                    PermissionResponse.PermissionInfoResponse res = new PermissionResponse.PermissionInfoResponse();
                    BeanUtils.copyProperties(p, res);
                    return res;
                })
                .collect(Collectors.toList());
        return ServiceResult.success(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<PermissionResponse.PermissionInfoResponse>> getPermissionsByModule(String module) {
        List<PermissionEntity> permissions = permissionRepository.findByModule(module);
        List<PermissionResponse.PermissionInfoResponse> responses = permissions.stream()
                .map(p -> {
                    PermissionResponse.PermissionInfoResponse res = new PermissionResponse.PermissionInfoResponse();
                    BeanUtils.copyProperties(p, res);
                    return res;
                })
                .collect(Collectors.toList());
        return ServiceResult.success(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<String>> getAllModules() {
        List<String> modules = permissionRepository.findDistinctModules();
        return ServiceResult.success(modules);
    }
}
