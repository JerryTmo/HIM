package com.example.service;

import com.example.config.ApiClientConfig;
import com.example.exception.ApiServiceException;
import com.example.util.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class SystemApiService {

    private static volatile SystemApiService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private SystemApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.baseUrl = ApiClientConfig.getBaseUrl();
    }

    public static SystemApiService getInstance() {
        if (instance == null) {
            synchronized (SystemApiService.class) {
                if (instance == null) {
                    instance = new SystemApiService();
                }
            }
        }
        return instance;
    }

    // ==================== 用户信息获取 ====================

    public void getCurrentUserInfo(Consumer<UserInfo> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/auth/me", null,
                new TypeReference<ServiceResult<UserInfo>>() {},
                result -> {
                    UserInfo userInfo = result.getData();
                    if (userInfo != null) {
                        UserSession.login(
                                UserSession.getAuthToken(),
                                userInfo.getUsername(),
                                userInfo.getId(),
                                userInfo.getRoles(),
                                userInfo.getPermissions()
                        );
                    }
                    onSuccess.accept(userInfo);
                },
                onError);
    }

    // ==================== 角色管理 ====================

    public void getAllRoles(Consumer<List<RoleInfo>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/roles", null,
                new TypeReference<ServiceResult<List<RoleInfo>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getRoleById(String id, Consumer<RoleDetail> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/roles/" + id, null,
                new TypeReference<ServiceResult<RoleDetail>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createRole(CreateRoleRequest request, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/roles", request,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updateRole(UpdateRoleRequest request, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/roles", request,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deleteRole(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/roles/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void assignPermissionsToRole(String roleId, List<String> permissionIds, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/roles/" + roleId + "/permissions", permissionIds,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void assignRolesToUser(String userId, List<String> roleIds, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/roles/assign/" + userId, roleIds,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 权限管理 ====================

    public void getAllPermissions(Consumer<List<PermissionInfo>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/permissions", null,
                new TypeReference<ServiceResult<List<PermissionInfo>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getPermissionById(String id, Consumer<PermissionDetail> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/permissions/" + id, null,
                new TypeReference<ServiceResult<PermissionDetail>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createPermission(CreatePermissionRequest request, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/permissions", request,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updatePermission(UpdatePermissionRequest request, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/permissions", request,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deletePermission(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/permissions/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void getPermissionsByModule(String module, Consumer<List<PermissionInfo>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/permissions/module/" + module, null,
                new TypeReference<ServiceResult<List<PermissionInfo>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getAllModules(Consumer<List<String>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/permissions/modules", null,
                new TypeReference<ServiceResult<List<String>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    // ==================== 菜单管理 ====================

    public void getMenus(Consumer<List<MenuDTO>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/menu/findByMenu", null,
                new TypeReference<ServiceResult<List<MenuDTO>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    // ==================== 私有方法 ====================

    private <T> void callAsync(String method, String path, Object body,
                               TypeReference<ServiceResult<T>> responseType,
                               Consumer<ServiceResult<T>> onSuccess,
                               Consumer<Throwable> onError) {
        Task<ServiceResult<T>> task = new Task<>() {
            @Override
            protected ServiceResult<T> call() throws Exception {
                return callSync(method, path, body, responseType);
            }
        };

        task.setOnSucceeded(e -> {
            ServiceResult<T> result = task.getValue();
            if (result.isSuccess()) {
                Platform.runLater(() -> onSuccess.accept(result));
            } else {
                Platform.runLater(() -> onError.accept(new ApiServiceException(result.getMessage())));
            }
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            Platform.runLater(() -> onError.accept(error));
        });

        new Thread(task).start();
    }

    private <T> ServiceResult<T> callSync(String method, String path, Object body,
                                          TypeReference<ServiceResult<T>> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");

        String token = UserSession.getAuthToken();
        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        if ("GET".equalsIgnoreCase(method)) {
            requestBuilder.GET();
        } else if ("POST".equalsIgnoreCase(method)) {
            String jsonBody = objectMapper.writeValueAsString(body);
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else if ("PUT".equalsIgnoreCase(method)) {
            String jsonBody = objectMapper.writeValueAsString(body);
            requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else if ("DELETE".equalsIgnoreCase(method)) {
            requestBuilder.DELETE();
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), responseType);
        } else if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new ApiServiceException("权限不足或未登录: " + response.statusCode());
        } else {
            throw new ApiServiceException("HTTP Error: " + response.statusCode());
        }
    }

    // ==================== 数据模型类 ====================

    public static class ServiceResult<T> {
        private boolean success;
        private String message;
        private T data;
        private Integer code;
        private Long timestamp;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }

    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private Set<String> roles;
        private Set<String> permissions;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }
        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    }

    public static class RoleInfo {
        private String id;
        private String name;
        private String code;
        private String description;
        private Boolean isDefault;
        private LocalDateTime createdAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getIsDefault() { return isDefault; }
        public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class RoleDetail extends RoleInfo {
        private List<PermissionInfo> permissions;

        public List<PermissionInfo> getPermissions() { return permissions; }
        public void setPermissions(List<PermissionInfo> permissions) { this.permissions = permissions; }
    }

    public static class PermissionInfo {
        private String id;
        private String name;
        private String code;
        private String module;
        private String description;
        private LocalDateTime createdAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getModule() { return module; }
        public void setModule(String module) { this.module = module; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class PermissionDetail extends PermissionInfo {
    }

    public static class CreateRoleRequest {
        private String name;
        private String code;
        private String description;
        private Boolean isDefault;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getIsDefault() { return isDefault; }
        public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    }

    public static class UpdateRoleRequest {
        private String id;
        private String name;
        private String description;
        private Boolean isDefault;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getIsDefault() { return isDefault; }
        public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    }

    public static class CreatePermissionRequest {
        private String name;
        private String code;
        private String module;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getModule() { return module; }
        public void setModule(String module) { this.module = module; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdatePermissionRequest {
        private String id;
        private String name;
        private String module;
        private String description;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getModule() { return module; }
        public void setModule(String module) { this.module = module; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // ==================== 菜单 DTO（與後端 MenuServiceImpl.MenuDTO 對應） ====================

    public static class MenuDTO {
        private String id;
        private String title;
        private String route;
        private String icon;
        private Integer sortOrder;
        private String module;
        private List<MenuDTO> children;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        public String getModule() { return module; }
        public void setModule(String module) { this.module = module; }
        public List<MenuDTO> getChildren() { return children; }
        public void setChildren(List<MenuDTO> children) { this.children = children; }
    }
}