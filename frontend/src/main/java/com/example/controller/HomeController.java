package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.util.DialogManager;
import com.example.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomeController {

    @FXML
    private Label usernameLabel;

    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        setupUserInfo();
    }

    private void setupUserInfo() {
        String username = UserSession.getUsername();
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText(username);
        }
    }

    @FXML
    private void handlePatientManagement() {
        App.navigateTo(AppPage.PATIENT_MANAGEMENT);
    }

    @FXML
    private void handleDoctorSchedule() {
        App.navigateTo(AppPage.DOCTOR_SCHEDULE);
    }

    @FXML
    private void handleMedicalRecord() {
        App.navigateTo(AppPage.MEDICAL_RECORD);
    }

    @FXML
    private void handleMedicineManagement() {
        App.navigateTo(AppPage.MEDICINE_MANAGEMENT);
    }

    @FXML
    private void handleAppointmentManagement() {
        App.navigateTo(AppPage.APPOINTMENT_MANAGEMENT);
    }

    @FXML
    private void handleSettings() {
        App.navigateTo(AppPage.SETTINGS);
    }

    @FXML
    private void handleLogout() {
        if (dialogManager.showConfirm("确认退出", "确定要退出系统吗？")) {
            UserSession.logout();
            App.navigateTo(AppPage.LOGIN);
        }
    }
}
