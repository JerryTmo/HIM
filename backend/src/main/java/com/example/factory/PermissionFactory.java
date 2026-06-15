package com.example.factory;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.entity.PermissionEntity;
import com.example.enums.PermissionType;

@Component
public class PermissionFactory {
    public PermissionEntity createPermission(String module, PermissionType type) {
        return PermissionEntity.builder()
                .name(type.generatePermissionName(module))
                .code(type.generatePermissionCode(module))
                .module(module)
                .description(type.generateDescription(module))
                .build();
    }

    public List<PermissionEntity> createAllPermissions(String module) {
        return PermissionType.getAll().stream()
                .map(type -> createPermission(module, type))
                .collect(Collectors.toList());
    }
}
