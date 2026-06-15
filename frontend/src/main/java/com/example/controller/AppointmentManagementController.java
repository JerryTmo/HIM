package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.Appointment;
import com.example.util.DialogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
public class AppointmentManagementController {

    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, String> idColumn;
    @FXML
    private TableColumn<Appointment, String> patientNameColumn;
    @FXML
    private TableColumn<Appointment, String> doctorNameColumn;
    @FXML
    private TableColumn<Appointment, String> departmentColumn;
    @FXML
    private TableColumn<Appointment, LocalDate> dateColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> timeColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;

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
    private DatePicker appointmentDatePicker;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField symptomsField;
    @FXML
    private TextArea notesArea;

    private ObservableList<Appointment> appointmentList;
    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        initializeTable();
        initializeComboBoxes();
        loadSampleData();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        appointmentList = FXCollections.observableArrayList();
        appointmentTable.setItems(appointmentList);
    }

    private void initializeComboBoxes() {
        departmentComboBox.setItems(FXCollections.observableArrayList(
            "内科", "外科", "儿科", "妇科", "骨科", "眼科", "耳鼻喉科", "皮肤科"));

        timeComboBox.setItems(FXCollections.observableArrayList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"));

        typeComboBox.setItems(FXCollections.observableArrayList("初诊", "复诊", "体检"));
    }

    private void loadSampleData() {
        Appointment app1 = new Appointment("A001", "P001", "D001", LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        app1.setPatientName("张三");
        app1.setDoctorName("王医生");
        app1.setDepartment("内科");
        app1.setAppointmentType("复诊");
        app1.setSymptoms("头晕、胸闷");
        appointmentList.add(app1);

        Appointment app2 = new Appointment("A002", "P002", "D002", LocalDate.now().plusDays(2), LocalTime.of(14, 30));
        app2.setPatientName("李四");
        app2.setDoctorName("李医生");
        app2.setDepartment("皮肤科");
        app2.setAppointmentType("初诊");
        app2.setSymptoms("皮肤瘙痒");
        appointmentList.add(app2);
    }

    @FXML
    private void handleAddAppointment() {
        try {
            Appointment appointment = new Appointment();
            appointment.setId("A" + String.format("%03d", appointmentList.size() + 1));
            appointment.setPatientId(patientIdField.getText());
            appointment.setPatientName(patientNameField.getText());
            appointment.setDoctorId(doctorIdField.getText());
            appointment.setDoctorName(doctorNameField.getText());
            appointment.setDepartment(departmentComboBox.getValue());
            appointment.setAppointmentDate(appointmentDatePicker.getValue());
            String timeStr = timeComboBox.getValue();
            if (timeStr != null) {
                String[] parts = timeStr.split(":");
                appointment.setAppointmentTime(LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            }
            appointment.setAppointmentType(typeComboBox.getValue());
            appointment.setSymptoms(symptomsField.getText());
            appointment.setNotes(notesArea.getText());

            appointmentList.add(appointment);
            clearForm();
            dialogManager.showInfo("预约信息添加成功！");
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            dialogManager.showWarning("请先选择要修改的预约");
            return;
        }

        selectedAppointment.setPatientId(patientIdField.getText());
        selectedAppointment.setPatientName(patientNameField.getText());
        selectedAppointment.setDoctorId(doctorIdField.getText());
        selectedAppointment.setDoctorName(doctorNameField.getText());
        selectedAppointment.setDepartment(departmentComboBox.getValue());
        selectedAppointment.setAppointmentDate(appointmentDatePicker.getValue());
        String timeStr = timeComboBox.getValue();
        if (timeStr != null) {
            String[] parts = timeStr.split(":");
            selectedAppointment.setAppointmentTime(LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }
        selectedAppointment.setAppointmentType(typeComboBox.getValue());
        selectedAppointment.setSymptoms(symptomsField.getText());
        selectedAppointment.setNotes(notesArea.getText());

        appointmentTable.refresh();
        dialogManager.showInfo("预约信息修改成功！");
    }

    @FXML
    private void handleDeleteAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            dialogManager.showWarning("请先选择要删除的预约");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该预约吗？")) {
            appointmentList.remove(selectedAppointment);
            clearForm();
            dialogManager.showInfo("预约删除成功！");
        }
    }

    @FXML
    private void handleConfirmAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            dialogManager.showWarning("请先选择预约");
            return;
        }
        selectedAppointment.setStatus("已确认");
        appointmentTable.refresh();
        dialogManager.showInfo("预约已确认！");
    }

    @FXML
    private void handleCompleteAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            dialogManager.showWarning("请先选择预约");
            return;
        }
        selectedAppointment.setStatus("已完成");
        appointmentTable.refresh();
        dialogManager.showInfo("预约已完成！");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            appointmentTable.setItems(appointmentList);
            return;
        }

        ObservableList<Appointment> filteredList = FXCollections.observableArrayList();
        for (Appointment appointment : appointmentList) {
            if ((appointment.getPatientName() != null && appointment.getPatientName().toLowerCase().contains(keyword)) ||
                (appointment.getDoctorName() != null && appointment.getDoctorName().toLowerCase().contains(keyword)) ||
                (appointment.getId() != null && appointment.getId().toLowerCase().contains(keyword))) {
                filteredList.add(appointment);
            }
        }
        appointmentTable.setItems(filteredList);
    }

    @FXML
    private void handleTableClick() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            patientIdField.setText(selectedAppointment.getPatientId());
            patientNameField.setText(selectedAppointment.getPatientName());
            doctorIdField.setText(selectedAppointment.getDoctorId());
            doctorNameField.setText(selectedAppointment.getDoctorName());
            departmentComboBox.setValue(selectedAppointment.getDepartment());
            appointmentDatePicker.setValue(selectedAppointment.getAppointmentDate());
            if (selectedAppointment.getAppointmentTime() != null) {
                timeComboBox.setValue(selectedAppointment.getAppointmentTime().toString());
            }
            typeComboBox.setValue(selectedAppointment.getAppointmentType());
            symptomsField.setText(selectedAppointment.getSymptoms());
            notesArea.setText(selectedAppointment.getNotes());
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
        appointmentDatePicker.setValue(null);
        timeComboBox.setValue(null);
        typeComboBox.setValue(null);
        symptomsField.clear();
        notesArea.clear();
        appointmentTable.getSelectionModel().clearSelection();
    }
}
