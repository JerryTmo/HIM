package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.MedicalRecord;
import com.example.util.DialogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class MedicalRecordController {

    @FXML
    private TableView<MedicalRecord> recordTable;
    @FXML
    private TableColumn<MedicalRecord, String> idColumn;
    @FXML
    private TableColumn<MedicalRecord, String> patientNameColumn;
    @FXML
    private TableColumn<MedicalRecord, String> doctorNameColumn;
    @FXML
    private TableColumn<MedicalRecord, String> departmentColumn;
    @FXML
    private TableColumn<MedicalRecord, LocalDate> visitDateColumn;
    @FXML
    private TableColumn<MedicalRecord, String> diagnosisColumn;
    @FXML
    private TableColumn<MedicalRecord, String> statusColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField patientIdField;
    @FXML
    private TextField patientNameField;
    @FXML
    private TextField doctorIdField;
    @FXML
    private TextField doctorNameField;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private DatePicker visitDatePicker;
    @FXML
    private TextField chiefComplaintField;
    @FXML
    private TextField diagnosisField;
    @FXML
    private TextArea treatmentPlanArea;
    @FXML
    private TextArea notesArea;

    private ObservableList<MedicalRecord> recordList;
    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        initializeTable();
        initializeDepartmentComboBox();
        loadSampleData();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        visitDateColumn.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        recordList = FXCollections.observableArrayList();
        recordTable.setItems(recordList);
    }

    private void initializeDepartmentComboBox() {
        departmentComboBox.setItems(FXCollections.observableArrayList(
            "内科", "外科", "儿科", "妇科", "骨科", "眼科", "耳鼻喉科", "皮肤科"));
    }

    private void loadSampleData() {
        MedicalRecord record1 = new MedicalRecord("R001", "P001", "D001", "感冒");
        record1.setPatientName("张三");
        record1.setDoctorName("王医生");
        record1.setDepartment("内科");
        record1.setVisitDate(LocalDate.now().minusDays(5));
        recordList.add(record1);

        MedicalRecord record2 = new MedicalRecord("R002", "P002", "D002", "皮肤过敏");
        record2.setPatientName("李四");
        record2.setDoctorName("李医生");
        record2.setDepartment("皮肤科");
        record2.setVisitDate(LocalDate.now().minusDays(3));
        recordList.add(record2);
    }

    @FXML
    private void handleAddRecord() {
        try {
            MedicalRecord record = new MedicalRecord();
            record.setId("R" + String.format("%03d", recordList.size() + 1));
            record.setPatientId(patientIdField.getText());
            record.setPatientName(patientNameField.getText());
            record.setDoctorId(doctorIdField.getText());
            record.setDoctorName(doctorNameField.getText());
            record.setDepartment(departmentComboBox.getValue());
            record.setVisitDate(visitDatePicker.getValue());
            record.setChiefComplaint(chiefComplaintField.getText());
            record.setDiagnosis(diagnosisField.getText());
            record.setTreatmentPlan(treatmentPlanArea.getText());
            record.setNotes(notesArea.getText());

            recordList.add(record);
            clearForm();
            dialogManager.showInfo("病历信息添加成功！");
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateRecord() {
        MedicalRecord selectedRecord = recordTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            dialogManager.showWarning("请先选择要修改的病历");
            return;
        }

        selectedRecord.setPatientId(patientIdField.getText());
        selectedRecord.setPatientName(patientNameField.getText());
        selectedRecord.setDoctorId(doctorIdField.getText());
        selectedRecord.setDoctorName(doctorNameField.getText());
        selectedRecord.setDepartment(departmentComboBox.getValue());
        selectedRecord.setVisitDate(visitDatePicker.getValue());
        selectedRecord.setChiefComplaint(chiefComplaintField.getText());
        selectedRecord.setDiagnosis(diagnosisField.getText());
        selectedRecord.setTreatmentPlan(treatmentPlanArea.getText());
        selectedRecord.setNotes(notesArea.getText());

        recordTable.refresh();
        dialogManager.showInfo("病历信息修改成功！");
    }

    @FXML
    private void handleDeleteRecord() {
        MedicalRecord selectedRecord = recordTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            dialogManager.showWarning("请先选择要删除的病历");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该病历吗？")) {
            recordList.remove(selectedRecord);
            clearForm();
            dialogManager.showInfo("病历删除成功！");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            recordTable.setItems(recordList);
            return;
        }

        ObservableList<MedicalRecord> filteredList = FXCollections.observableArrayList();
        for (MedicalRecord record : recordList) {
            if ((record.getPatientName() != null && record.getPatientName().toLowerCase().contains(keyword)) ||
                (record.getDoctorName() != null && record.getDoctorName().toLowerCase().contains(keyword)) ||
                (record.getDiagnosis() != null && record.getDiagnosis().toLowerCase().contains(keyword)) ||
                (record.getId() != null && record.getId().toLowerCase().contains(keyword))) {
                filteredList.add(record);
            }
        }
        recordTable.setItems(filteredList);
    }

    @FXML
    private void handleTableClick() {
        MedicalRecord selectedRecord = recordTable.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            patientIdField.setText(selectedRecord.getPatientId());
            patientNameField.setText(selectedRecord.getPatientName());
            doctorIdField.setText(selectedRecord.getDoctorId());
            doctorNameField.setText(selectedRecord.getDoctorName());
            departmentComboBox.setValue(selectedRecord.getDepartment());
            visitDatePicker.setValue(selectedRecord.getVisitDate());
            chiefComplaintField.setText(selectedRecord.getChiefComplaint());
            diagnosisField.setText(selectedRecord.getDiagnosis());
            treatmentPlanArea.setText(selectedRecord.getTreatmentPlan());
            notesArea.setText(selectedRecord.getNotes());
        }
    }

    @FXML
    private void handleBack() {
        App.navigateTo(AppPage.HOME);
    }

    private void clearForm() {
        patientIdField.clear();
        patientNameField.clear();
        doctorIdField.clear();
        doctorNameField.clear();
        departmentComboBox.setValue(null);
        visitDatePicker.setValue(null);
        chiefComplaintField.clear();
        diagnosisField.clear();
        treatmentPlanArea.clear();
        notesArea.clear();
        recordTable.getSelectionModel().clearSelection();
    }
}
