package com.example.util;

import com.example.config.ApiClientConfig;

import java.util.HashSet;
import java.util.Set;

public class UserSession {
    private static String authToken;
    private static String username;
    private static String userId;
    private static Set<String> roles = new HashSet<>();
    private static Set<String> permissions = new HashSet<>();

    public static void login(String token, String user, String id, Set<String> userRoles, Set<String> userPermissions) {
        authToken = token;
        username = user;
        userId = id;
        roles = (userRoles != null) ? new HashSet<>(userRoles) : new HashSet<>();
        permissions = (userPermissions != null) ? new HashSet<>(userPermissions) : new HashSet<>();

        // 更新 ApiClient 的認證信息
        ApiClientConfig.setAuthToken(token);
    }

    public static void logout() {
        authToken = null;
        username = null;
        userId = null;
        roles.clear();
        permissions.clear();

        // 清除 ApiClient 的認證信息
        ApiClientConfig.clearAuthToken();
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static String getUsername() {
        return username;
    }

    public static String getUserId() {
        return userId;
    }

    public static Set<String> getRoles() {
        return new HashSet<>(roles);
    }

    public static Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }

    public static boolean isLoggedIn() {
        return authToken != null;
    }

    public static boolean hasRole(String role) {
        return roles.contains(role);
    }

    public static boolean hasAnyRole(String... checkRoles) {
        for (String role : checkRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public static boolean hasAnyPermission(String... checkPermissions) {
        for (String permission : checkPermissions) {
            if (permissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}