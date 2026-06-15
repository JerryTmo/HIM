package com.example.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUtils {
    private final UserRepository userRepository;

    /**
     * 獲取用戶信息
     * 
     * @return 用戶結果
     */
    public UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    /**
     * 獲取用戶ID
     * 
     * @return 用戶ID
     */
    public String getUserId() {
        UserDetails userDetails = getUserDetails();
        String username = userDetails.getUsername();
        return this.userRepository.findIdByUsername(username);
    }
}
