package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.Doctor;
import com.example.util.DialogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class DoctorScheduleController {

    @FXML
    private TableView<Doctor> doctorTable;
    @FXML
    private TableColumn<Doctor, String> idColumn;
    @FXML
    private TableColumn<Doctor, String> nameColumn;
    @FXML
    private TableColumn<Doctor, String> departmentColumn;
    @FXML
    private TableColumn<Doctor, String> titleColumn;
    @FXML
    private TableColumn<Doctor, String> phoneColumn;
    @FXML
    private TableColumn<Doctor, String> statusColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField specialtyField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<String> scheduleListView;

    private ObservableList<Doctor> doctorList;
    private ObservableList<String> scheduleList;
    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        initializeTable();
        initializeScheduleList();
        loadSampleData();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        doctorList = FXCollections.observableArrayList();
        doctorTable.setItems(doctorList);
    }

    private void initializeScheduleList() {
        scheduleList = FXCollections.observableArrayList();
        scheduleListView.setItems(scheduleList);
    }

    private void loadSampleData() {
        Doctor doc1 = new Doctor("D001", "王医生", "内科", "主任医师");
        doc1.setPhone("13900139000");
        doc1.setSpecialty("心血管疾病");
        doc1.setAvailableTimes(Arrays.asList("周一 上午", "周三 下午", "周五 上午"));
        doctorList.add(doc1);

        Doctor doc2 = new Doctor("D002", "李医生", "皮肤科", "副主任医师");
        doc2.setPhone("13900139001");
        doc2.setSpecialty("皮肤过敏、湿疹");
        doc2.setAvailableTimes(Arrays.asList("周二 全天", "周四 上午"));
        doctorList.add(doc2);
    }

    @FXML
    private void handleAddDoctor() {
        try {
            Doctor doctor = new Doctor();
            doctor.setId("D" + String.format("%03d", doctorList.size() + 1));
            doctor.setName(nameField.getText());
            doctor.setDepartment(departmentField.getText());
            doctor.setTitle(titleField.getText());
            doctor.setPhone(phoneField.getText());
            doctor.setSpecialty(specialtyField.getText());
            doctor.setDescription(descriptionArea.getText());

            doctorList.add(doctor);
            clearForm();
            dialogManager.showInfo("医生信息添加成功！");
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateDoctor() {
        Doctor selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) {
            dialogManager.showWarning("请先选择要修改的医生");
            return;
        }

        selectedDoctor.setName(nameField.getText());
        selectedDoctor.setDepartment(departmentField.getText());
        selectedDoctor.setTitle(titleField.getText());
        selectedDoctor.setPhone(phoneField.getText());
        selectedDoctor.setSpecialty(specialtyField.getText());
        selectedDoctor.setDescription(descriptionArea.getText());

        doctorTable.refresh();
        dialogManager.showInfo("医生信息修改成功！");
    }

    @FXML
    private void handleDeleteDoctor() {
        Doctor selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) {
            dialogManager.showWarning("请先选择要删除的医生");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该医生吗？")) {
            doctorList.remove(selectedDoctor);
            clearForm();
            dialogManager.showInfo("医生删除成功！");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            doctorTable.setItems(doctorList);
            return;
        }

        ObservableList<Doctor> filteredList = FXCollections.observableArrayList();
        for (Doctor doctor : doctorList) {
            if ((doctor.getName() != null && doctor.getName().toLowerCase().contains(keyword)) ||
                (doctor.getDepartment() != null && doctor.getDepartment().toLowerCase().contains(keyword)) ||
                (doctor.getId() != null && doctor.getId().toLowerCase().contains(keyword))) {
                filteredList.add(doctor);
            }
        }
        doctorTable.setItems(filteredList);
    }

    @FXML
    private void handleTableClick() {
        Doctor selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            nameField.setText(selectedDoctor.getName());
            departmentField.setText(selectedDoctor.getDepartment());
            titleField.setText(selectedDoctor.getTitle());
            phoneField.setText(selectedDoctor.getPhone());
            specialtyField.setText(selectedDoctor.getSpecialty());
            descriptionArea.setText(selectedDoctor.getDescription());

            scheduleList.clear();
            if (selectedDoctor.getAvailableTimes() != null) {
                scheduleList.addAll(selectedDoctor.getAvailableTimes());
            }
        }
    }

    @FXML
    private void handleBack() {
        App.navigateTo(AppPage.HOME);
    }

    private void clearForm() {
        nameField.clear();
        departmentField.clear();
        titleField.clear();
        phoneField.clear();
        specialtyField.clear();
        descriptionArea.clear();
        scheduleList.clear();
        doctorTable.getSelectionModel().clearSelection();
    }
}
