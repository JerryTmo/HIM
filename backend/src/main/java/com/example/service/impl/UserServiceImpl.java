package com.example.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.common.ServiceResult;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.LoginResponse;
import com.example.entity.UserEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.util.JwtUtil;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ServiceResult<LoginResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // 2. 查询用户信息
            UserEntity user = userRepository.findByUsernameWithRoles(request.getUsername())
                    .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
            // 3. 生成JWT令牌
            String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRoleSet());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRoleSet());

            // 4. 构建响应
            LoginResponse response = LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
                    .build();

            return ServiceResult.success(response);
        } catch (BadCredentialsException e) {
            throw new BusinessException(ResultCode.WRONG_PASSWORD);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new BusinessException(ResultCode.ERROR, e.getMessage());
        }
    }

    @Override
    public ServiceResult<String> register(RegisterRequest request) {

        // 检查用户名是否存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .status(1)
                .build();
        Assert.notNull(userEntity, "用戶信息不能為空");
        // 保存用戶
        UserEntity savedUser = userRepository.save(userEntity);
        // TODO Auto-generated method stub
        return ServiceResult.success(savedUser.getUsername());
    }

}
