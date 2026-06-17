package com.example.service.impl;

import com.example.common.ServiceResult;
import com.example.dto.response.AIResponse.*;
import com.example.entity.AIConversationEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.AIConversationRepository;
import com.example.service.AIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIConversationRepository conversationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Value("${ai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:deepseek-chat}")
    private String model;

    // ==================== Chat ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<ChatResponse> chat(String userId, String message, String sessionId) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackChat(message);
        }

        String sid = sessionId != null ? sessionId : UUID.randomUUID().toString().substring(0, 8);

        // 构建上下文消息
        List<Map<String, String>> messages = buildChatContext(userId, sid);
        messages.add(Map.of("role", "user", "content", message));

        try {
            String reply = callAI(messages);
            saveMessage(userId, "user", message, sid, "chat");
            saveMessage(userId, "assistant", reply, sid, "chat");

            return ServiceResult.success(ChatResponse.builder()
                    .reply(reply)
                    .sessionId(sid)
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("AI chat error", e);
            return ServiceResult.error("AI 服务暂时不可用，请稍后再试");
        }
    }

    // ==================== Mind Map ====================

    @Override
    public ServiceResult<MindMapResponse> generateMindMap(String topic, String context) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackMindMap(topic);
        }

        String prompt = buildMindMapPrompt(topic, context);
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "你是一个医疗知识专家。请将用户提供的主题生成为结构化的JSON思维导图数据。"
                        + "必须严格按照以下格式返回，不要包含任何其他文字：\n"
                        + "{\"centralTopic\":\"主题\",\"children\":[{\"title\":\"分支1\",\"children\":[{\"title\":\"子项\"}]}]}"),
                Map.of("role", "user", "content", prompt)
        );

        try {
            String reply = callAI(messages);
            MindMapResponse parsed = parseMindMapResponse(reply, topic);
            return ServiceResult.success(parsed);
        } catch (Exception e) {
            log.error("AI mind map error", e);
            return fallbackMindMap(topic);
        }
    }

    // ==================== Triage ====================

    @Override
    public ServiceResult<TriageResponse> triage(String symptoms, Integer age, String gender) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackTriage(symptoms);
        }

        String prompt = buildTriagePrompt(symptoms, age, gender);
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "你是一名经验丰富的分诊护士。根据患者症状、年龄、性别，推荐合适的就诊科室和 urgency。"
                        + "必须严格按照以下JSON格式返回，不要包含其他文字：\n"
                        + "{\"department\":\"科室\",\"doctorSuggestion\":\"建议\",\"urgency\":\"immediate|soon|routine\",\"description\":\"分析\"}"),
                Map.of("role", "user", "content", prompt)
        );

        try {
            String reply = callAI(messages);
            TriageResponse parsed = parseTriageResponse(reply);
            return ServiceResult.success(parsed);
        } catch (Exception e) {
            log.error("AI triage error", e);
            return fallbackTriage(symptoms);
        }
    }

    // ==================== History ====================

    @Override
    public ServiceResult<List<ConversationHistory>> getHistory(String userId, String sessionId) {
        List<AIConversationEntity> records;
        if (sessionId != null) {
            records = conversationRepository.findByUserIdAndSessionIdOrderByCreatedAtAsc(userId, sessionId);
        } else {
            records = conversationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, "chat");
        }

        List<ConversationHistory> list = records.stream()
                .map(r -> ConversationHistory.builder()
                        .id(r.getId())
                        .role(r.getRole())
                        .content(r.getContent())
                        .sessionId(r.getSessionId())
                        .type(r.getType())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ServiceResult.success(list);
    }

    // ==================== Private ====================

    private String callAI(List<Map<String, String>> messages) throws Exception {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        String json = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("AI API error: {} {}", response.statusCode(), response.body());
            throw new BusinessException(ResultCode.ERROR);
        }

        JsonNode root = objectMapper.readTree(response.body());
        return root.get("choices").get(0).get("message").get("content").asText();
    }

    private List<Map<String, String>> buildChatContext(String userId, String sessionId) {
        List<AIConversationEntity> recent = conversationRepository
                .findByUserIdAndSessionIdOrderByCreatedAtAsc(userId, sessionId);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "你是一个专业的医疗助手，名叫"医小助"。你擅长回答医疗健康相关问题、提供就医建议。"
                        + "注意：你提供的建议仅供参考，不能替代专业医生的诊断。紧急情况请立即就医。"));

        // 取最近10条历史
        recent.stream()
                .skip(Math.max(0, recent.size() - 10))
                .forEach(r -> messages.add(Map.of("role", r.getRole(), "content", r.getContent())));

        return messages;
    }

    private void saveMessage(String userId, String role, String content, String sessionId, String type) {
        conversationRepository.save(AIConversationEntity.builder()
                .userId(userId)
                .role(role)
                .content(content)
                .sessionId(sessionId)
                .type(type)
                .build());
    }

    private String buildMindMapPrompt(String topic, String context) {
        StringBuilder sb = new StringBuilder("主题：" + topic);
        if (context != null && !context.isBlank()) {
            sb.append("\n上下文：").append(context);
        }
        sb.append("\n请生成层级结构的思维导图JSON数据，要求：");
        sb.append("\n1. 第一层3-5个分支");
        sb.append("\n2. 每个分支2-4个子节点");
        sb.append("\n3. 节点名称简洁");
        return sb.toString();
    }

    private String buildTriagePrompt(String symptoms, Integer age, String gender) {
        StringBuilder sb = new StringBuilder("患者症状：" + symptoms);
        if (age != null) sb.append("\n年龄：").append(age);
        if (gender != null) sb.append("\n性别：").append(gender);
        return sb.toString();
    }

    private MindMapResponse parseMindMapResponse(String json, String fallbackTopic) {
        try {
            // 尝试提取 JSON
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}') + 1;
            if (start >= 0 && end > start) {
                json = json.substring(start, end);
            }
            JsonNode root = objectMapper.readTree(json);
            return MindMapResponse.builder()
                    .centralTopic(root.get("centralTopic").asText())
                    .children(parseNodes(root.get("children")))
                    .build();
        } catch (Exception e) {
            log.warn("Parse mind map failed, use fallback", e);
            return fallbackMindMap(fallbackTopic).getData();
        }
    }

    private List<MindMapNode> parseNodes(JsonNode arr) {
        List<MindMapNode> list = new ArrayList<>();
        if (arr == null || !arr.isArray()) return list;
        for (JsonNode item : arr) {
            List<MindMapNode> children = parseNodes(item.get("children"));
            list.add(MindMapNode.builder()
                    .title(item.get("title").asText())
                    .children(children.isEmpty() ? null : children)
                    .build());
        }
        return list;
    }

    private TriageResponse parseTriageResponse(String json) {
        try {
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}') + 1;
            if (start >= 0 && end > start) {
                json = json.substring(start, end);
            }
            JsonNode root = objectMapper.readTree(json);
            return TriageResponse.builder()
                    .department(root.get("department").asText())
                    .doctorSuggestion(getOrEmpty(root, "doctorSuggestion"))
                    .urgency(getOrEmpty(root, "urgency"))
                    .description(getOrEmpty(root, "description"))
                    .build();
        } catch (Exception e) {
            log.warn("Parse triage failed, use fallback", e);
            return fallbackTriage("").getData();
        }
    }

    private String getOrEmpty(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    // ==================== Fallbacks (无API Key时的模拟数据) ====================

    private ServiceResult<ChatResponse> fallbackChat(String message) {
        String reply;
        if (message.contains("咳嗽") || message.contains("发烧")) {
            reply = "根据您描述的咳嗽/发烧症状，建议您：\n\n"
                    + "1️⃣ **就诊科室**：建议挂呼吸内科或全科门诊\n"
                    + "2️⃣ **注意事项**：就诊前请佩戴口罩，避免交叉感染\n"
                    + "3️⃣ **自我观察**：注意体温变化，多喝温水\n\n"
                    + "⚠️ *以上建议仅供参考，请以医生诊断为准*";
        } else if (message.contains("头痛") || message.contains("头晕")) {
            reply = "关于您提到的头痛/头晕症状：\n\n"
                    + "1️⃣ **可能原因**：紧张性头痛、偏头痛、颈椎问题等\n"
                    + "2️⃣ **建议科室**：神经内科 ≥ 疼痛科\n"
                    + "3️⃣ **建议检查**：血压测量、头颅CT（如症状持续）\n\n"
                    + "⚠️ *如果症状突然加重，请立即就医*";
        } else if (message.contains("胃") || message.contains("肚子")) {
            reply = "关于消化系统不适：\n\n"
                    + "1️⃣ **建议科室**：消化内科\n"
                    + "2️⃣ **建议检查**：胃镜、幽门螺杆菌检测\n"
                    + "3️⃣ **饮食建议**：清淡饮食，避免辛辣刺激\n\n"
                    + "⚠️ *以上建议仅供参考*";
        } else {
            reply = "您好！我是医小助 🤖\n\n"
                    + "您可以向我咨询以下内容：\n"
                    + "• 🏥 **症状咨询**：描述您的症状，我帮您分析\n"
                    + "• 💊 **用药查询**：查询药品信息\n"
                    + "• 📋 **就医建议**：推荐科室和就诊方向\n\n"
                    + "请问有什么可以帮助您的？";
        }
        return ServiceResult.success(ChatResponse.builder()
                .reply(reply)
                .sessionId(UUID.randomUUID().toString().substring(0, 8))
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    private ServiceResult<MindMapResponse> fallbackMindMap(String topic) {
        return ServiceResult.success(MindMapResponse.builder()
                .centralTopic(topic)
                .children(List.of(
                        node("诊断方法", List.of(node("体格检查"), node("实验室检查"), node("影像学检查"))),
                        node("治疗方案", List.of(node("药物治疗"), node("手术治疗"), node("康复治疗"))),
                        node("预防措施", List.of(node("定期体检"), node("健康生活"), node("早期筛查")))
                ))
                .build());
    }

    private ServiceResult<TriageResponse> fallbackTriage(String symptoms) {
        String dept;
        String urgency;
        if (symptoms.contains("胸") || symptoms.contains("心")) {
            dept = "心血管内科";
            urgency = "immediate";
        } else if (symptoms.contains("烧") || symptoms.contains("咳")) {
            dept = "呼吸内科";
            urgency = "soon";
        } else if (symptoms.contains("肚子") || symptoms.contains("胃") || symptoms.contains("腹")) {
            dept = "消化内科";
            urgency = "soon";
        } else if (symptoms.contains("头")) {
            dept = "神经内科";
            urgency = "soon";
        } else if (symptoms.contains("骨") || symptoms.contains("腰") || symptoms.contains("腿")) {
            dept = "骨科";
            urgency = "routine";
        } else {
            dept = "全科门诊";
            urgency = "routine";
        }
        return ServiceResult.success(TriageResponse.builder()
                .department(dept)
                .doctorSuggestion("建议尽快就诊")
                .urgency(urgency)
                .description("基于您描述的症状，初步推荐就诊科室为" + dept + "。")
                .build());
    }

    private static MindMapNode node(String title) {
        return MindMapNode.builder().title(title).build();
    }

    private static MindMapNode node(String title, List<MindMapNode> children) {
        return MindMapNode.builder().title(title).children(children).build();
    }
}
