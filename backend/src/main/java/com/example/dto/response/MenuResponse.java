package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MenuResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "菜单树节点")
    public static class MenuDTO {
        private String id;
        private String title;
        private String route;
        private String icon;
        private Integer sortOrder;
        private Boolean isActive;
        private String module;
        private String parentId;
        private String parentTitle;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<MenuDTO> children;
    }
}
