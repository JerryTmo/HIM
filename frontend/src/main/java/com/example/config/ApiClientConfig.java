package com.example.config;

import io.swagger.client.ApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiClientConfig {
    private static final Properties props = new Properties();
    private static String activeProfile;

    private static ApiClient insClient;
    private static final Object lock = new Object();

    static {
        loadProperties();
        // 可以通過系統變數或環境變數指定活躍的設定檔
        activeProfile = System.getProperty("api.profile", "dev");
    }

    private static void loadProperties() {
        try (InputStream input = ApiClientConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                return;
            }
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ApiClient getConfiguredClient() {
        if (insClient == null) {
            synchronized (lock) {
                if (insClient == null) {
                    insClient = createClient();
                }
            }
        }
        return insClient;
    }

    private static ApiClient createClient() {
        ApiClient client = new ApiClient();

        // 根據活躍的設定檔取得對應的基礎路徑
        String basePath = props.getProperty("api.base.path." + activeProfile,
                "http://localhost:8080");
        client.setBasePath(basePath);

        // 設定超時
        int connectTimeout = Integer.parseInt(
                props.getProperty("api.connect.timeout", "30000"));
        client.setConnectTimeout(connectTimeout);

        int readTimeout = Integer.parseInt(
                props.getProperty("api.read.timeout", "30000"));
        client.setReadTimeout(readTimeout);

        // 是否啟用調試模式
        boolean debug = Boolean.parseBoolean(
                props.getProperty("api.debug", "false"));
        client.setDebugging(debug);

        return client;
    }

    /**
     * 更新认证 token（用于登录后设置）
     */
    public static void setAuthToken(String token) {
        ApiClient client = getConfiguredClient();
        if (token != null && !token.isEmpty()) {
            client.setBearerToken(token);
        }
    }

    /**
     * 清除认证 token（用于登出）
     */
    public static void clearAuthToken() {
        ApiClient client = getConfiguredClient();
        client.setBearerToken((String) null);
    }

    /**
     * 获取当前 token（用于调试）
     */
    public static String getAuthToken() {
        ApiClient client = getConfiguredClient();
        // 注意：ApiClient 可能没有直接获取 token 的方法，需要根据实际情况调整
        return null;
    }

    public static void setActiveProfile(String profile) {
        activeProfile = profile;
        // 切换 profile 时重置 client 实例
        synchronized (lock) {
            insClient = null;
        }
    }
}