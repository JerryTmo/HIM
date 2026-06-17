package com.example.controller;

import com.example.config.ApiClientConfig;
import com.example.util.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AI 聊天助手控制器 — 医小助
 */
@Slf4j
public class AIChatController {

    @FXML private ScrollPane messageScrollPane;
    @FXML private VBox messageContainer;
    @FXML private TextField inputField;
    @FXML private Button sendBtn;
    @FXML private Button newChatBtn;
    @FXML private HBox suggestionBar;

    private String currentSessionId;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        currentSessionId = UUID.randomUUID().toString().substring(0, 8);
        inputField.setOnAction(e -> handleSend());
        showWelcomeMessage();
    }

    private void showWelcomeMessage() {
        addAssistantMessage("您好！我是 **医小助** 🤖\n\n"
                + "我可以帮您：\n"
                + "• 🏥 分析症状，推荐就诊科室\n"
                + "• 💊 查询药品信息和用法\n"
                + "• 📋 提供就医建议和注意事项\n\n"
                + "请问有什么可以帮助您的？");
    }

    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        inputField.clear();
        sendBtn.setDisable(true);

        addUserMessage(message);
        addTypingIndicator();

        Task<AIResult> task = new Task<>() {
            @Override
            protected AIResult call() throws Exception {
                return callChatApi(message);
            }
        };
        task.setOnSucceeded(e -> {
            removeTypingIndicator();
            AIResult result = task.getValue();
            if (result != null && result.success) {
                addAssistantMessage(result.reply);
            } else {
                addAssistantMessage("抱歉，我暂时无法回答您的问题。请稍后再试。");
            }
            sendBtn.setDisable(false);
        });
        task.setOnFailed(e -> {
            removeTypingIndicator();
            addAssistantMessage("网络错误，请检查连接后重试。");
            sendBtn.setDisable(false);
        });
        new Thread(task).start();
    }

    private AIResult callChatApi(String message) throws Exception {
        String baseUrl = ApiClientConfig.getBaseUrl();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("sessionId", currentSessionId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/ai/chat"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + UserSession.getAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // 解析 ServiceResult<ChatResponse>
        var root = mapper.readTree(response.body());
        AIResult r = new AIResult();
        r.success = root.get("success").asBoolean();
        if (r.success && root.has("data")) {
            r.reply = root.get("data").get("reply").asText();
            if (root.get("data").has("sessionId")) {
                currentSessionId = root.get("data").get("sessionId").asText();
            }
        } else {
            r.reply = root.has("message") ? root.get("message").asText() : "未知错误";
        }
        return r;
    }

    @FXML
    private void handleNewChat() {
        currentSessionId = UUID.randomUUID().toString().substring(0, 8);
        messageContainer.getChildren().clear();
        showWelcomeMessage();
    }

    @FXML
    private void handleSuggestion(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        inputField.setText((String) btn.getUserData());
        handleSend();
    }

    // ==================== UI 消息渲染 ====================

    private void addUserMessage(String text) {
        HBox row = new HBox();
        row.setAlignment(Pos.TOP_RIGHT);
        row.setPadding(new javafx.geometry.Insets(0, 0, 0, 0));

        TextFlow bubble = new TextFlow();
        bubble.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
        bubble.setMaxWidth(500);
        bubble.setStyle("-fx-background-color: #6c5ce7; -fx-background-radius: 18 18 4 18;");
        Text t = new Text(text);
        t.setStyle("-fx-fill: white; -fx-font-size: 14px;");
        bubble.getChildren().add(t);

        row.getChildren().add(bubble);
        messageContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void addAssistantMessage(String text) {
        TextFlow bubble = new TextFlow();
        bubble.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
        bubble.setMaxWidth(500);
        bubble.setStyle("-fx-background-color: white; -fx-background-radius: 18 18 18 4; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        String[] segments = text.split("\\*\\*");
        for (int i = 0; i < segments.length; i++) {
            Text t = new Text(segments[i]);
            String style = "-fx-font-size: 14px; -fx-fill: #2d3436;";
            if (i % 2 == 1) style += "-fx-font-weight: bold;";
            t.setStyle(style);
            bubble.getChildren().add(t);
        }

        HBox row = new HBox();
        row.setAlignment(Pos.TOP_LEFT);
        row.getChildren().add(bubble);
        messageContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void addTypingIndicator() {
        HBox row = new HBox();
        row.setAlignment(Pos.TOP_LEFT);
        row.setId("typingIndicator");

        Label typing = new Label("🤖 正在输入...");
        typing.setStyle("-fx-background-color: white; -fx-background-radius: 18; "
                + "-fx-padding: 12 18; -fx-font-size: 13px; -fx-text-fill: #b2bec3; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        row.getChildren().add(typing);
        messageContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void removeTypingIndicator() {
        messageContainer.getChildren().removeIf(n -> "typingIndicator".equals(n.getId()));
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            messageScrollPane.setVvalue(1.0);
        });
    }

    private static class AIResult {
        boolean success;
        String reply;
    }
}
