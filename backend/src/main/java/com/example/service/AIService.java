package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.response.AIResponse.*;

import java.util.List;

public interface AIService {

    /**
     * AI 对话（带上下文）
     */
    ServiceResult<ChatResponse> chat(String userId, String message, String sessionId);

    /**
     * 生成思维导图
     */
    ServiceResult<MindMapResponse> generateMindMap(String topic, String context);

    /**
     * 智能导诊
     */
    ServiceResult<TriageResponse> triage(String symptoms, Integer age, String gender);

    /**
     * 获取对话历史
     */
    ServiceResult<List<ConversationHistory>> getHistory(String userId, String sessionId);
}
