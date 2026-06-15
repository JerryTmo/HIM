package com.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LogController implements Initializable {

    // FXML 注入的组件
    @FXML
    private Label currentTimeLabel;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    @FXML
    private TextField extraField;
    @FXML
    private Button saveBtn;
    @FXML
    private Button clearBtn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterLevelCombo;
    @FXML
    private ComboBox<String> filterCategoryCombo;
    @FXML
    private ListView<String> logListView;
    @FXML
    private TextArea detailArea;
    @FXML
    private Button deleteBtn;

    @FXML
    private Label statsLabel;
    @FXML
    private Label saveStatusLabel;

    @FXML
    private ToggleGroup levelGroup;
    @FXML
    private RadioButton infoRadio;
    @FXML
    private RadioButton warningRadio;
    @FXML
    private RadioButton errorRadio;
    @FXML
    private RadioButton debugRadio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 初始化时间标签
        updateCurrentTime();

        // 设置分类下拉框的默认项（如果FXML没设置）
        if (categoryCombo.getItems().isEmpty()) {
            categoryCombo.setItems(FXCollections.observableArrayList(
                    "系统", "业务", "用户操作", "数据库", "网络", "安全"));
        }

        // 设置筛选级别下拉框
        if (filterLevelCombo.getItems().isEmpty()) {
            filterLevelCombo.setItems(FXCollections.observableArrayList(
                    "全部", "INFO", "WARNING", "ERROR", "DEBUG"));
        }

        // 设置默认选中 INFO
        infoRadio.setSelected(true);

        // 添加列表选择监听
        logListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLogDetail(newValue));
    }

    // ========== 事件处理方法 ==========

    @FXML
    private void saveLog() {
        // 获取日志级别
        String level = "INFO"; // 默认
        RadioButton selectedRadio = (RadioButton) levelGroup.getSelectedToggle();
        if (selectedRadio != null) {
            if (selectedRadio == warningRadio)
                level = "WARNING";
            else if (selectedRadio == errorRadio)
                level = "ERROR";
            else if (selectedRadio == debugRadio)
                level = "DEBUG";
        }

        String category = categoryCombo.getValue();
        String title = titleField.getText();
        String content = contentArea.getText();
        String extra = extraField.getText();

        // 简单验证
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("错误", "标题和内容不能为空！");
            return;
        }

        // 格式化日志条目
        String logEntry = String.format("[%s] %s - %s: %s", level, category, title, content);
        if (!extra.isEmpty()) {
            logEntry += " | 附加: " + extra;
        }

        // 添加到列表
        logListView.getItems().add(logEntry);

        // 更新统计
        updateStats();

        // 显示保存成功
        saveStatusLabel.setText("✅ 日志保存成功！");

        // 清空表单
        clearForm();
    }

    @FXML
    private void clearForm() {
        titleField.clear();
        contentArea.clear();
        extraField.clear();
        categoryCombo.setValue(null);
        infoRadio.setSelected(true);
        saveStatusLabel.setText("");
    }

    @FXML
    private void refreshLogList() {
        // 这里刷新日志列表（如果数据来自文件或数据库）
        // 示例：重新从某个数据源加载
        saveStatusLabel.setText("🔄 列表已刷新");
    }

    @FXML
    private void exportAllLogs() {
        if (logListView.getItems().isEmpty()) {
            showAlert("提示", "没有日志可导出！");
            return;
        }

        // 导出逻辑（示例：显示在控制台）
        System.out.println("=== 导出日志 ===");
        for (String log : logListView.getItems()) {
            System.out.println(log);
        }
        saveStatusLabel.setText("📥 日志已导出");
    }

    @FXML
    private void deleteOldLogs() {
        // 清理旧日志逻辑
        logListView.getItems().clear();
        detailArea.clear();
        updateStats();
        saveStatusLabel.setText("🗑️ 旧日志已清理");
    }

    @FXML
    private void deleteSelectedLog() {
        String selected = logListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logListView.getItems().remove(selected);
            detailArea.clear();
            updateStats();
            saveStatusLabel.setText("🗑️ 已删除选中日志");
        }
    }

    // ========== 辅助方法 ==========

    private void updateCurrentTime() {
        // 更新时间显示
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss");
        currentTimeLabel.setText("当前时间: " + now.format(formatter));
    }

    private void showLogDetail(String log) {
        if (log != null) {
            detailArea.setText(log);
        }
    }

    private void updateStats() {
        int count = logListView.getItems().size();
        statsLabel.setText("共 " + count + " 条日志");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}