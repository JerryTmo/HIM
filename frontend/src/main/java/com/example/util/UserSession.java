package com.example.util;

import com.example.config.ApiClientConfig;

import io.swagger.client.ApiClient;

public class UserSession {
    private static String authToken;
    private static String username;

    public static void login(String token, String user) {
        authToken = token;
        username = user;

        // 更新 ApiClient 的認證信息
        ApiClientConfig.setAuthToken(token);
    }

    public static void logout() {
        authToken = null;
        username = null;

        // 清除 ApiClient 的認證信息
        ApiClientConfig.clearAuthToken();
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return authToken != null;
    }
}