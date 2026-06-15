package com.example.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.common.ServiceResult;
import com.example.dto.response.PhoneResponse.InnerPhoneResponse;

/**
 * 相冊模塊
 */
public interface PhoneService {

    /**
     * 初始化
     */
    ServiceResult<List<InnerPhoneResponse>> getInit();

    ServiceResult<Integer> insertPhone(List<MultipartFile> files);
}
