package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

public class AIRequest {

    @Data
    @Schema(description = "AI 对话请求")
    public static class ChatRequest {
        @NotBlank(message = "消息内容不能为空")
        @Schema(description = "用户消息", example = "感冒了应该看什么科？")
        private String message;

        @Schema(description = "会话ID（续传历史传此值）")
        private String sessionId;
    }

    @Data
    @Schema(description = "AI 思维导图生成请求")
    public static class MindMapRequest {
        @NotBlank(message = "主题不能为空")
        @Schema(description = "思维导图主题", example = "高血压治疗方案")
        private String topic;

        @Schema(description = "额外上下文信息")
        private String context;
    }

    @Data
    @Schema(description = "AI 智能导诊请求")
    public static class TriageRequest {
        @NotBlank(message = "症状描述不能为空")
        @Schema(description = "患者症状描述", example = "头痛、发烧38.5度、咳嗽三天")
        private String symptoms;

        @Schema(description = "患者年龄")
        private Integer age;

        @Schema(description = "患者性别")
        private String gender;
    }
}
