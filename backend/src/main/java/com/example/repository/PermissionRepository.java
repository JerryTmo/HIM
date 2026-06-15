package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String> {
    /**
     * 根據權限代碼查找
     */
    Optional<PermissionEntity> findByCode(String code);

    /**
     * 根據模塊查找所有權限
     */
    List<PermissionEntity> findByModule(String module);

    /**
     * 根據模塊和操作類型查找
     */
    Optional<PermissionEntity> findByModuleAndCodeContaining(String module, String operation);
}
