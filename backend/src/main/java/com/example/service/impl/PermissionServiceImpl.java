package com.example.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest.UpdateCodeRequest;
import com.example.entity.PermissionEntity;
import com.example.entity.RoleEntity;
import com.example.enums.PermissionType;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.factory.PermissionFactory;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import com.example.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionFactory permissionFactory;
    private final RoleRepository roleRepository;

    @Transactional
    public Set<PermissionEntity> getOrCreatePermissions(String module) {
        return PermissionType.getAll().stream()
                .map(type -> getOrCreatePermission(module, type))
                .collect(Collectors.toSet());
    }

    private PermissionEntity getOrCreatePermission(String module, PermissionType type) {
        String code = type.generatePermissionCode(module);
        return permissionRepository.findByCode(code).orElseGet(() -> {
            PermissionEntity newPermission = permissionFactory.createPermission(module, type);
            return permissionRepository.save(newPermission);
        });
    }

    public List<String> getPermissionCodes(String module) {
        return PermissionType.getAll().stream()
                .map(type -> type.generatePermissionCode(module))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResult<Integer> updateCode(UpdateCodeRequest updateCodeRequest) {
        log.info("id:{}", updateCodeRequest.getRoleId());
        // 檢查角色是否存在
        RoleEntity roleEntity = roleRepository.findById(updateCodeRequest.getRoleId())
                .orElseThrow(() -> new BusinessException(ResultCode.ROLE_NOT_FOUND));
        // 檢查權限是否存在
        Set<PermissionEntity> permissionEntities = permissionRepository
                .findAllById(updateCodeRequest.getPermissionIds())
                .stream().collect(Collectors.toSet());
        // 驗證所有請求的權限ID都存在
        if (permissionEntities.size() != updateCodeRequest.getPermissionIds().size()) {
            Set<String> foundIds = permissionEntities.stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toSet());
            Set<String> notFoundIds = updateCodeRequest.getPermissionIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            log.warn("部分權限不存在: {}", notFoundIds);
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND, notFoundIds + "");
        }
        // 更新角色權限（直接替換整個集合）
        roleEntity.getPermissions().clear(); // 清除現有權限
        roleEntity.getPermissions().addAll(permissionEntities); // 添加新權限

        // 保存（會自動更新中間表）
        roleRepository.save(roleEntity);

        log.info("角色權限更新成功: roleId={}, 權限數量={}",
                updateCodeRequest.getRoleId(), permissionEntities.size());
        return ServiceResult.success(permissionEntities.size());
    }
}
