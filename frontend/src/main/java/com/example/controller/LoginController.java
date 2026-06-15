package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ResourceBundle;

import com.example.App;
import com.example.exception.ApiServiceException;
import com.example.menu.AppPage;
import com.example.service.ApiService;
import com.example.util.UserSession;

import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.LoginRequest;
import io.swagger.client.model.ServiceResultLoginResponse;
import lombok.NoArgsConstructor;

import java.net.URL;

@NoArgsConstructor
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private VBox loginVBox;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // 初始化時清空錯誤信息
        errorLabel.setText("");

        // 添加輸入框監聽，輸入時清除錯誤信息
        usernameField.textProperty().addListener((obs, old, newVal) -> {
            errorLabel.setText("");
        });

        passwordField.textProperty().addListener((obs, old, newVal) -> {
            errorLabel.setText("");
        });

        // 設置回車鍵也可以登錄
        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!validateInput(username, password))
            return;
        // 創建請求對象
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // 調用API
        ApiService.getInstance().callAsync(() -> {
            DefaultApi api = ApiService.getInstance().getApi(DefaultApi.class);
            return api.login(loginRequest);
        },
                this::handleLoginSuccess,
                this::handleError,
                this::showLoading);
    }

    private void handleLoginSuccess(ServiceResultLoginResponse result) {
        if (result.getCode() == 200) {
            String token = result.getData().getAccessToken();
            UserSession.login(token, usernameField.getText());
            App.navigateTo(AppPage.HOME);
        } else {
            showError(result.getMessage() != null ? result.getMessage() : "登录失败");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private void handleError(Throwable error) {
        showError(error.getMessage());

        if (error instanceof ApiServiceException) {
            Integer httpCode = ((ApiServiceException) error).getHttpCode();
            if (httpCode != null && httpCode == 401) {
                passwordField.clear();
                passwordField.requestFocus();
            }
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showError("请输入用户名");
            usernameField.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            showError("请输入密码");
            passwordField.requestFocus();
            return false;
        }
        return true;
    }

    // 把原來的 showLoading 改成：
    private void showLoading(boolean isLoading) {
        loginButton.setDisable(isLoading);
        // 如果需要，可以根據 isLoading 做不同處理
        if (!isLoading) {
            errorLabel.setText("");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    @FXML
    private void goToRegister() {
        // 導航到註冊頁面
        // App.navigateTo(AppPage.REGISTER);
        errorLabel.setText("注册功能开发中");
    }

    @FXML
    private void handleForgotPassword() {
        errorLabel.setText("密码重置功能开发中");
    }
}