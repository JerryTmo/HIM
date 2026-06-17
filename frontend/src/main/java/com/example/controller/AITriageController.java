package com.example.controller;

import com.example.config.ApiClientConfig;
import com.example.util.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI 智能导诊控制器
 */
@Slf4j
public class AITriageController {

    @FXML private TextArea symptomsArea;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderBox;
    @FXML private Button triageBtn;
    @FXML private VBox resultBox;
    @FXML private VBox loadingBox;
    @FXML private Label deptLabel;
    @FXML private Label urgencyLabel;
    @FXML private Label descLabel;
    @FXML private Label suggestionLabel;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        genderBox.getItems().addAll("男", "女");
        genderBox.setValue("男");
        resultBox.setVisible(false);
        resultBox.setManaged(false);
    }

    @FXML
    private void handleTriage() {
        String symptoms = symptomsArea.getText().trim();
        if (symptoms.isEmpty()) {
            showAlert("请输入症状描述");
            return;
        }

        triageBtn.setDisable(true);
        loadingBox.setVisible(true);
        loadingBox.setManaged(true);
        resultBox.setVisible(false);
        resultBox.setManaged(false);

        Task<TriageResult> task = new Task<>() {
            @Override
            protected TriageResult call() throws Exception {
                return callTriageApi(symptoms);
            }
        };
        task.setOnSucceeded(e -> {
            loadingBox.setVisible(false);
            loadingBox.setManaged(false);
            triageBtn.setDisable(false);

            TriageResult result = task.getValue();
            if (result != null && result.success) {
                showResult(result);
            } else {
                showAlert("导诊服务暂时不可用，请稍后再试");
            }
        });
        task.setOnFailed(e -> {
            loadingBox.setVisible(false);
            loadingBox.setManaged(false);
            triageBtn.setDisable(false);
            showAlert("网络错误，请检查连接");
        });
        new Thread(task).start();
    }

    private TriageResult callTriageApi(String symptoms) throws Exception {
        String baseUrl = ApiClientConfig.getBaseUrl();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("symptoms", symptoms);
        if (!ageField.getText().isBlank()) {
            body.put("age", Integer.parseInt(ageField.getText()));
        }
        body.put("gender", genderBox.getValue());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/ai/triage"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + UserSession.getAuthToken())
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var root = mapper.readTree(response.body());

        TriageResult r = new TriageResult();
        r.success = root.get("success").asBoolean();
        if (r.success && root.has("data")) {
            var data = root.get("data");
            r.department = data.get("department").asText();
            r.urgency = data.get("urgency").asText();
            r.description = data.has("description") ? data.get("description").asText() : "";
            r.doctorSuggestion = data.has("doctorSuggestion") ? data.get("doctorSuggestion").asText() : "";
        }
        return r;
    }

    private void showResult(TriageResult r) {
        deptLabel.setText(r.department);
        urgencyLabel.setText(switch (r.urgency) {
            case "immediate" -> "🆘 紧急 - 请立即就医";
            case "soon" -> "⚡ 尽快 - 建议24小时内就诊";
            default -> "📅 常规 - 可择期就诊";
        });
        urgencyLabel.setStyle(switch (r.urgency) {
            case "immediate" -> "-fx-background-color: #ff7675; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 6 16; -fx-font-size: 14px; -fx-font-weight: bold;";
            case "soon" -> "-fx-background-color: #fdcb6e; -fx-text-fill: #2d3436; -fx-background-radius: 12; -fx-padding: 6 16; -fx-font-size: 14px; -fx-font-weight: bold;";
            default -> "-fx-background-color: #55efc4; -fx-text-fill: #2d3436; -fx-background-radius: 12; -fx-padding: 6 16; -fx-font-size: 14px; -fx-font-weight: bold;";
        });
        descLabel.setText(r.description);
        suggestionLabel.setText(r.doctorSuggestion);

        resultBox.setVisible(true);
        resultBox.setManaged(true);
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("智能导诊");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    private static class TriageResult {
        boolean success;
        String department;
        String urgency;
        String description;
        String doctorSuggestion;
    }
}
