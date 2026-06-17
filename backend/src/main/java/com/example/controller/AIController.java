package com.example.controller;

import com.example.annotation.RequirePermission;
import com.example.common.ServiceResult;
import com.example.dto.request.AIRequest.*;
import com.example.dto.response.AIResponse.*;
import com.example.service.AIService;
import com.example.util.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI 智能助手")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final UserUtils userUtils;

    @PostMapping("/chat")
    @Operation(summary = "AI 对话（带上下文记忆）")
    @RequirePermission("ai:chat")
    public ServiceResult<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String username = userUtils.getUserDetails().getUsername();
        return aiService.chat(username, request.getMessage(), request.getSessionId());
    }

    @PostMapping("/mindmap")
    @Operation(summary = "AI 生成思维导图")
    @RequirePermission("ai:mindmap")
    public ServiceResult<MindMapResponse> generateMindMap(@Valid @RequestBody MindMapRequest request) {
        return aiService.generateMindMap(request.getTopic(), request.getContext());
    }

    @PostMapping("/triage")
    @Operation(summary = "AI 智能导诊（症状→科室推荐）")
    @RequirePermission("ai:triage")
    public ServiceResult<TriageResponse> triage(@Valid @RequestBody TriageRequest request) {
        return aiService.triage(request.getSymptoms(), request.getAge(), request.getGender());
    }

    @GetMapping("/history")
    @Operation(summary = "获取对话历史")
    @RequirePermission("ai:chat")
    public ServiceResult<List<ConversationHistory>> getHistory(
            @RequestParam(required = false) String sessionId) {
        String username = userUtils.getUserDetails().getUsername();
        return aiService.getHistory(username, sessionId);
    }
}
