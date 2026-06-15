package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PermissionResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfoResponse {
        private String id;
        private String name;
        private String code;
        private String module;
        private String description;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionDetailResponse {
        private String id;
        private String name;
        private String code;
        private String module;
        private String description;
        private LocalDateTime createdAt;
    }
}
