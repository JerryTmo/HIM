package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.Patient;
import com.example.util.DialogManager;
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

    private ObservableList<Patient> patientList;
    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        initializeTable();
        initializeGenderComboBox();
        loadSampleData();
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

    private void loadSampleData() {
        patientList.add(new Patient("P001", "张三", "男", 35, "13800138000"));
        patientList.add(new Patient("P002", "李四", "女", 28, "13800138001"));
        patientList.add(new Patient("P003", "王五", "男", 45, "13800138002"));
    }

    @FXML
    private void handleAddPatient() {
        try {
            Patient patient = new Patient();
            patient.setId("P" + String.format("%03d", patientList.size() + 1));
            patient.setName(nameField.getText());
            patient.setPhone(phoneField.getText());
            patient.setGender(genderComboBox.getValue());
            patient.setAge(ageField.getText() != null && !ageField.getText().isEmpty() ? Integer.parseInt(ageField.getText()) : 0);
            patient.setIdCard(idCardField.getText());
            patient.setAddress(addressField.getText());

            patientList.add(patient);
            clearForm();
            dialogManager.showInfo("患者信息添加成功！");
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdatePatient() {
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

        patientTable.refresh();
        dialogManager.showInfo("患者信息修改成功！");
    }

    @FXML
    private void handleDeletePatient() {
        Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            dialogManager.showWarning("请先选择要删除的患者");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该患者吗？")) {
            patientList.remove(selectedPatient);
            clearForm();
            dialogManager.showInfo("患者删除成功！");
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
