package com.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class QuickGenerateController implements Initializable {

    @FXML
    private ToggleButton textModeBtn;
    @FXML
    private ToggleButton voiceModeBtn;
    @FXML
    private VBox textInputSection;
    @FXML
    private VBox voiceInputSection;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private ChoiceBox<String> layoutChoiceBox;
    @FXML
    private ChoiceBox<String> themeChoiceBox;
    @FXML
    private HBox aiStatusBox;
    @FXML
    private ProgressIndicator aiProgress;
    @FXML
    private Label aiStatusLabel;
    @FXML
    private Label voiceStatusLabel;
    @FXML
    private TextArea recognizedText;
    @FXML
    private Button voiceRecordBtn;
    @FXML
    private Label statusLabel;

    private boolean isRecording = false;
    private List<Stage> openWindows = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupModeToggle();
        setupChoiceBoxes();
    }

    private void setupModeToggle() {
        ToggleGroup modeGroup = new ToggleGroup();
        textModeBtn.setToggleGroup(modeGroup);
        voiceModeBtn.setToggleGroup(modeGroup);

        modeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isTextMode = newVal == textModeBtn;
            textInputSection.setVisible(isTextMode);
            textInputSection.setManaged(isTextMode);
            voiceInputSection.setVisible(!isTextMode);
            voiceInputSection.setManaged(!isTextMode);
        });
    }

    private void setupChoiceBoxes() {
        layoutChoiceBox.setItems(FXCollections.observableArrayList(
                "🌳 树形布局", "☀️ 辐射布局", "🏢 组织布局"));
        layoutChoiceBox.setValue("🌳 树形布局");

        themeChoiceBox.setItems(FXCollections.observableArrayList(
                "🎨 紫色梦幻", "🌿 自然绿色", "🌊 海洋蓝色", "🔥 热情红色", "🌙 暗夜模式"));
        themeChoiceBox.setValue("🎨 紫色梦幻");
    }

    @FXML
    private void generateMindMap() {
        String inputText = inputTextArea.getText().trim();
        if (inputText.isEmpty()) {
            showAlert("提示", "请先输入内容！");
            return;
        }

        // 显示AI处理状态
        aiStatusBox.setVisible(true);
        aiStatusBox.setManaged(true);
        statusLabel.setText("⏳ AI正在分析...");

        // 获取选择的布局和主题
        String layout = layoutChoiceBox.getValue();
        String theme = themeChoiceBox.getValue();

        // 模拟AI处理
        new Thread(() -> {
            try {
                // 模拟AI分析时间
                for (int i = 0; i <= 100; i += 10) {
                    final int progress = i;
                    Thread.sleep(200);
                    Platform.runLater(() -> {
                        aiProgress.setProgress(progress / 100.0);
                        aiStatusLabel.setText("AI正在分析... " + progress + "%");
                    });
                }

                Platform.runLater(() -> {
                    // 解析文本生成思维导图数据
                    MindMapData mindMapData = parseTextToMindMap(inputText);

                    // 在新窗口打开思维导图
                    openMindMapWindow(mindMapData, layout, theme);

                    // 恢复状态
                    aiStatusBox.setVisible(false);
                    aiStatusBox.setManaged(false);
                    aiProgress.setProgress(0);
                    statusLabel.setText("✅ 思维导图已在新窗口生成");
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private MindMapData parseTextToMindMap(String text) {
        MindMapData data = new MindMapData();
        data.rootNode = new MindNode("思维导图", 0);

        // 按句号、换行、分号分割
        String[] sentences = text.split("[。\\n；;]");

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty())
                continue;

            // 查找包含关系的句子
            if (sentence.contains("包含") || sentence.contains("包括") ||
                    sentence.contains("有") || sentence.contains("分为")) {

                String[] parts = sentence.split("[包含包括有分为]");
                if (parts.length >= 2) {
                    String parent = parts[0].trim();
                    String childrenStr = parts[1].trim();

                    // 查找或创建父节点
                    MindNode parentNode = findOrCreateNode(data.rootNode, parent, 1);

                    // 分割子节点
                    String[] children = childrenStr.split("[、，,和及与以及]");
                    for (String child : children) {
                        child = child.trim().replaceAll("等$", "").replaceAll("等分支$", "");
                        if (!child.isEmpty() && child.length() > 1) {
                            addChildNode(parentNode, child);
                        }
                    }
                }
            } else {
                // 作为一级主题
                if (sentence.length() > 2) {
                    addChildNode(data.rootNode, sentence);
                }
            }
        }

        // 如果没有解析出子节点，创建一些演示节点
        if (data.rootNode.children.isEmpty()) {
            MindNode demoNode = new MindNode(text.substring(0, Math.min(15, text.length())), 1);
            demoNode.children.add(new MindNode("要点一", 2));
            demoNode.children.add(new MindNode("要点二", 2));
            demoNode.children.add(new MindNode("要点三", 2));
            data.rootNode.children.add(demoNode);
        }

        return data;
    }

    private MindNode findOrCreateNode(MindNode parent, String name, int level) {
        for (MindNode child : parent.children) {
            if (child.name.contains(name) || name.contains(child.name)) {
                return child;
            }
        }
        MindNode newNode = new MindNode(name, level);
        parent.children.add(newNode);
        return newNode;
    }

    private void addChildNode(MindNode parent, String name) {
        MindNode node = new MindNode(name, parent.level + 1);
        parent.children.add(node);
    }

    private void openMindMapWindow(MindMapData data, String layout, String theme) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/mindmap-view.fxml"));
            Parent root = loader.load();

            MindMapViewerController viewerController = loader.getController();
            viewerController.setMindMapData(data, layout, theme);

            Stage stage = new Stage();
            stage.setTitle("🧠 思维导图 - AI生成");
            stage.setScene(new Scene(root, 1200, 800));

            // 添加CSS
            stage.getScene().getStylesheets().add(
                    getClass().getResource("/com/example/css/mindmap-style.css").toExternalForm());

            // 设置窗口图标和最小尺寸
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            // 窗口居中
            stage.centerOnScreen();

            // 跟踪打开的窗口
            openWindows.add(stage);
            stage.setOnCloseRequest(e -> openWindows.remove(stage));

            stage.show();
            stage.toFront();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "无法打开思维导图窗口: " + e.getMessage());
        }
    }

    @FXML
    private void toggleVoiceRecord() {
        isRecording = !isRecording;
        if (isRecording) {
            voiceRecordBtn.setText("🔴 录音中...");
            voiceRecordBtn.setStyle(
                    "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 50; -fx-cursor: hand; -fx-padding: 20 40;");
            voiceStatusLabel.setText("🎙️ 正在录音，请说话...");

            // 模拟语音识别
            simulateVoiceRecognition();
        } else {
            voiceRecordBtn.setText("点击开始录音");
            voiceRecordBtn.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 50; -fx-cursor: hand; -fx-padding: 20 40;");
            voiceStatusLabel.setText("✅ 录音已停止");
        }
    }

    private void simulateVoiceRecognition() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    recognizedText.setText("人工智能包含机器学习、深度学习和自然语言处理。" +
                            "机器学习包括监督学习、无监督学习和强化学习。");
                    voiceStatusLabel.setText("✅ 语音识别完成");
                    isRecording = false;
                    voiceRecordBtn.setText("点击开始录音");
                    voiceRecordBtn.setStyle(
                            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 50; -fx-cursor: hand; -fx-padding: 20 40;");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void clearInput() {
        inputTextArea.clear();
        recognizedText.clear();
        statusLabel.setText("✅ 就绪");
    }

    @FXML
    private void showHistory() {
        // 显示历史记录窗口
        showAlert("历史记录", "历史记录功能开发中...");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}