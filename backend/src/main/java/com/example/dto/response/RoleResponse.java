package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RoleResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfoResponse {
        private String id;
        private String name;
        private String code;
        private String description;
        private Boolean isDefault;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDetailResponse {
        private String id;
        private String name;
        private String code;
        private String description;
        private Boolean isDefault;
        private LocalDateTime createdAt;
        private List<PermissionInfoResponse> permissions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfoResponse {
        private String id;
        private String name;
        private String code;
        private String module;
        private String description;
    }
}
