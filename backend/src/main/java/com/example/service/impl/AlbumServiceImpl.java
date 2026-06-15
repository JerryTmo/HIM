package com.example.service.impl;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.AlbumRequest.InnerAlbumRequest;
import com.example.dto.request.AlbumRequest.UpdateAlbumRequest;
import com.example.dto.response.AlbumResponse.GetInitResponse;
import com.example.entity.AlbumEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.AlbumRepository;
import com.example.repository.UserRepository;
import com.example.service.AlbumService;
import com.example.util.UserUtils;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserUtils userUtils;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> insertAlbum(InnerAlbumRequest innerAlbumRequest) {
        // 通過上下文獲取用戶信息
        String userId = getUserId(innerAlbumRequest.getName());
        AlbumEntity albumEntity = AlbumEntity.builder()
                .userId(userId)
                .name(innerAlbumRequest.getName())
                .description(innerAlbumRequest.getDescription())
                .coverImageUrl(innerAlbumRequest.getCoverImageUrl())
                .privacyType(innerAlbumRequest.getPrivacyType())
                .photoCount(0)
                .build();
        this.albumRepository.save(albumEntity);
        return ServiceResult.success(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事務
    public void updateAlbum(UpdateAlbumRequest updateAlbumRequest) {
        // 1. 參數校驗
        Assert.notNull(updateAlbumRequest.getId(), "ID不能為空");

        // 2. 查詢現有數據（必須是 Entity，不是 DTO）
        AlbumEntity albumEntity = this.albumRepository
                .findById(updateAlbumRequest.getId())
                .orElseThrow(() -> new RuntimeException("專輯不存在，ID: " + updateAlbumRequest.getId()));

        // 3. 更新非空字段
        BeanUtils.copyProperties(updateAlbumRequest, albumEntity,
                getNullPropertyNames(updateAlbumRequest));

        // 4. 設置更新時間（如果 Entity 有這個字段）
        albumEntity.setUpdatedAt(LocalDateTime.now());

        // 5. 保存更新（在事務中會自動更新，但顯式調用更清晰）
        this.albumRepository.save(albumEntity);
    }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }

    /**
     * 獲取用戶名ID
     * 並且檢查當前用戶下相冊是否存在
     * 
     * @return 用戶ID
     */
    private String getUserId(String name) {
        // 通過上下文獲取用戶信息
        UserDetails userDetails = this.userUtils.getUserDetails();
        String username = userDetails.getUsername();
        String userId = this.userRepository.findIdByUsername(username);
        // 檢查是否存在
        boolean exists = albumRepository.existsByNameAndUserId(
                name,
                userId);
        if (exists) {
            throw new BusinessException(ResultCode.ALBUM_NAME_EXISTS); // 需要定義對應的錯誤碼
        }
        return userId;
    }

    @Override
    public ServiceResult<List<GetInitResponse>> getInit() {
        // 獲取用戶Id
        UserDetails userDetails = this.userUtils.getUserDetails();
        String username = userDetails.getUsername();
        String userId = this.userRepository.findIdByUsername(username);
        List<GetInitResponse> getInitResponse = this.albumRepository.findGetInit(userId);
        // 空值檢測
        if (CollectionUtils.isEmpty(getInitResponse)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(getInitResponse);
    }

}
