package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.LoginResponse;

public interface UserService {
    /**
     * 登錄
     * 
     * @param request 用戶賬戶密碼
     * @return jwt
     */
    ServiceResult<LoginResponse> login(LoginRequest request);

    /**
     * 註冊
     * 
     * @param user 用戶信息
     * @return 註冊結果
     */
    ServiceResult<String> register(RegisterRequest request);
}
