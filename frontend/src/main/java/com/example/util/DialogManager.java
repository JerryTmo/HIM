package com.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar;

/**
 * 統一的對話框管理器
 */
public class DialogManager {

    private static DialogManager instance;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (instance == null) {
            instance = new DialogManager();
        }
        return instance;
    }

    /**
     * 根據後端返回結果顯示對話框
     */
    public void showResultDialog(Object result) {
        try {
            Integer code = getCodeValue(result);
            String message = getMessageValue(result);

            if (code == null) {
                showSystemError("無法解析後端返回結果");
                return;
            }

            if (code == 200) {
                showSuccess(message != null ? message : "操作成功");
            } else if (code >= 400 && code < 500) {
                showClientError(code, message != null ? message : "客戶端錯誤");
            } else if (code >= 500) {
                showServerError(code, message != null ? message : "伺服器錯誤");
            } else {
                showInfo(message != null ? message : "操作完成");
            }

        } catch (Exception e) {
            showSystemError("顯示對話框時發生錯誤：" + e.getMessage());
        }
    }

    /**
     * ✅ 新增：顯示錯誤對話框（根據 HTTP 狀態碼）
     */
    public void showError(int httpCode, String message) {
        if (httpCode >= 400 && httpCode < 500) {
            showClientError(httpCode, message);
        } else if (httpCode >= 500) {
            showServerError(httpCode, message);
        } else {
            showSystemError(message);
        }
    }

    /**
     * 顯示成功對話框
     */
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f0fff0;");

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }

        alert.showAndWait();
    }

    /**
     * 顯示客戶端錯誤 (4xx)
     */
    private void showClientError(int code, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("請求錯誤 (" + code + ")");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #fff3cd;");

        alert.showAndWait();
    }

    /**
     * 顯示伺服器錯誤 (5xx)
     */
    private void showServerError(int code, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("伺服器錯誤 (" + code + ")");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n\n請稍後再試或聯繫系統管理員");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f8d7da;");

        alert.showAndWait();
    }

    /**
     * 顯示系統錯誤
     */
    private void showSystemError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("系統錯誤");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #f8d7da;");

        alert.showAndWait();
    }

    /**
     * 顯示一般資訊
     */
    public void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 顯示警告
     */
    public void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 顯示確認對話框
     */
    public boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("確定", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("取消", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        return alert.showAndWait().orElse(noButton) == yesButton;
    }

    // ==================== 反射方法 ====================

    private Integer getCodeValue(Object obj) {
        try {
            String[] possibleMethods = { "getCode", "getStatusCode", "getStatus", "getErrorCode" };
            for (String methodName : possibleMethods) {
                try {
                    java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
                    Object value = method.invoke(obj);
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMessageValue(Object obj) {
        try {
            String[] possibleMethods = { "getMessage", "getMsg", "getError", "getErrorMessage" };
            for (String methodName : possibleMethods) {
                try {
                    java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
                    Object value = method.invoke(obj);
                    if (value instanceof String) {
                        return (String) value;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}