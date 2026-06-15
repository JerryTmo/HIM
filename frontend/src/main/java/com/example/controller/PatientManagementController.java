package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.Patient;
import com.example.service.MedicalApiService;
import com.example.util.DialogManager;
import com.example.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PatientManagementController {

    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, String> idColumn;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> genderColumn;
    @FXML
    private TableColumn<Patient, Integer> ageColumn;
    @FXML
    private TableColumn<Patient, String> phoneColumn;
    @FXML
    private TableColumn<Patient, String> statusColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField idCardField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField ageField;
    @FXML
    private TextField addressField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Patient> patientList;
    private DialogManager dialogManager;
    private MedicalApiService medicalApiService;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        medicalApiService = MedicalApiService.getInstance();
        initializeTable();
        initializeGenderComboBox();
        applyPermissionControl();
        loadPatientsFromServer();
    }

    private void applyPermissionControl() {
        // 检查权限并控制按钮的可见性和可用性
        boolean canCreate = UserSession.hasPermission("patient:create");
        boolean canUpdate = UserSession.hasPermission("patient:update");
        boolean canDelete = UserSession.hasPermission("patient:delete");
        boolean canRead = UserSession.hasPermission("patient:read");

        // 设置按钮状态
        addButton.setVisible(canCreate);
        updateButton.setVisible(canUpdate);
        deleteButton.setVisible(canDelete);

        // 如果没有读取权限，可以禁用表格或整个界面
        patientTable.setDisable(!canRead);
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        patientList = FXCollections.observableArrayList();
        patientTable.setItems(patientList);
    }

    private void initializeGenderComboBox() {
        genderComboBox.setItems(FXCollections.observableArrayList("男", "女"));
    }

    private void loadPatientsFromServer() {
        medicalApiService.getAllPatients(
                patients -> {
                    patientList.clear();
                    patientList.addAll(patients);
                },
                error -> {
                    log.error("加载患者列表失败", error);
                    dialogManager.showError(500, "加载患者列表失败：" + error.getMessage());
                }
        );
    }

    @FXML
    private void handleAddPatient() {
        // 双重检查权限
        if (!UserSession.hasPermission("patient:create")) {
            dialogManager.showError(403, "您没有创建患者的权限");
            return;
        }

        try {
            Patient patient = new Patient();
            patient.setName(nameField.getText());
            patient.setPhone(phoneField.getText());
            patient.setGender(genderComboBox.getValue());
            patient.setAge(ageField.getText() != null && !ageField.getText().isEmpty() ? Integer.parseInt(ageField.getText()) : 0);
            patient.setIdCard(idCardField.getText());
            patient.setAddress(addressField.getText());

            medicalApiService.createPatient(patient,
                    id -> {
                        patient.setId(id);
                        patientList.add(patient);
                        clearForm();
                        dialogManager.showInfo("患者信息添加成功！");
                    },
                    error -> {
                        log.error("添加患者失败", error);
                        dialogManager.showError(500, "添加失败：" + error.getMessage());
                    }
            );
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdatePatient() {
        // 双重检查权限
        if (!UserSession.hasPermission("patient:update")) {
            dialogManager.showError(403, "您没有修改患者的权限");
            return;
        }

        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            dialogManager.showWarning("请先选择要修改的患者");
            return;
        }

        selectedPatient.setName(nameField.getText());
        selectedPatient.setPhone(phoneField.getText());
        selectedPatient.setGender(genderComboBox.getValue());
        selectedPatient.setAge(ageField.getText() != null && !ageField.getText().isEmpty() ? Integer.parseInt(ageField.getText()) : 0);
        selectedPatient.setIdCard(idCardField.getText());
        selectedPatient.setAddress(addressField.getText());

        medicalApiService.updatePatient(selectedPatient,
                _void -> {
                    patientTable.refresh();
                    dialogManager.showInfo("患者信息修改成功！");
                },
                error -> {
                    log.error("更新患者失败", error);
                    dialogManager.showError(500, "更新失败：" + error.getMessage());
                }
        );
    }

    @FXML
    private void handleDeletePatient() {
        // 双重检查权限
        if (!UserSession.hasPermission("patient:delete")) {
            dialogManager.showError(403, "您没有删除患者的权限");
            return;
        }

        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            dialogManager.showWarning("请先选择要删除的患者");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该患者吗？")) {
            medicalApiService.deletePatient(selectedPatient.getId(),
                    _void -> {
                        patientList.remove(selectedPatient);
                        clearForm();
                        dialogManager.showInfo("患者删除成功！");
                    },
                    error -> {
                        log.error("删除患者失败", error);
                        dialogManager.showError(500, "删除失败：" + error.getMessage());
                    }
            );
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            patientTable.setItems(patientList);
            return;
        }

        ObservableList<Patient> filteredList = FXCollections.observableArrayList();
        for (Patient patient : patientList) {
            if ((patient.getName() != null && patient.getName().toLowerCase().contains(keyword)) ||
                (patient.getPhone() != null && patient.getPhone().contains(keyword)) ||
                (patient.getId() != null && patient.getId().toLowerCase().contains(keyword))) {
                filteredList.add(patient);
            }
        }
        patientTable.setItems(filteredList);
    }

    @FXML
    private void handleTableClick() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            nameField.setText(selectedPatient.getName());
            phoneField.setText(selectedPatient.getPhone());
            genderComboBox.setValue(selectedPatient.getGender());
            ageField.setText(selectedPatient.getAge() > 0 ? String.valueOf(selectedPatient.getAge()) : "");
            idCardField.setText(selectedPatient.getIdCard());
            addressField.setText(selectedPatient.getAddress());
        }
    }

    @FXML
    private void handleBack() {
        App.navigateTo(AppPage.HOME);
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        genderComboBox.setValue(null);
        ageField.clear();
        idCardField.clear();
        addressField.clear();
        patientTable.getSelectionModel().clearSelection();
    }
}
