package com.example.service;

import java.util.List;
import java.util.Set;

import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest.UpdateCodeRequest;
import com.example.entity.PermissionEntity;

public interface PermissionService {
    /**
     * 獲取權限
     * 
     * @param module 模型
     * @return 權限數據
     */
    Set<PermissionEntity> getOrCreatePermissions(String module);

    /**
     * 獲取權限代碼
     * 
     * @param module 模組
     * @return 權限代碼
     */
    List<String> getPermissionCodes(String module);

    ServiceResult<Integer> updateCode(UpdateCodeRequest updateCodeRequest);
}
