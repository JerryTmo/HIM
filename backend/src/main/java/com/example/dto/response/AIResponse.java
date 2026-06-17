package com.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AIResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AI 对话响应")
    public static class ChatResponse {
        private String reply;
        private String sessionId;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "思维导图数据")
    public static class MindMapResponse {
        private String centralTopic;
        private List<MindMapNode> children;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MindMapNode {
        private String title;
        private List<MindMapNode> children;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "智能导诊响应")
    public static class TriageResponse {
        private String department;       // 推荐科室
        private String doctorSuggestion; // 医生建议
        private String urgency;          //  urgency: "immediate" | "soon" | "routine"
        private String description;      // 分析说明
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "对话历史条目")
    public static class ConversationHistory {
        private String id;
        private String role;
        private String content;
        private String sessionId;
        private String type;
        private LocalDateTime createdAt;
    }
}
