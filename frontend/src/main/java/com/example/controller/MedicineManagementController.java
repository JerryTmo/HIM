package com.example.controller;

import com.example.App;
import com.example.menu.AppPage;
import com.example.model.Medicine;
import com.example.util.DialogManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MedicineManagementController {

    @FXML
    private TableView<Medicine> medicineTable;
    @FXML
    private TableColumn<Medicine, String> idColumn;
    @FXML
    private TableColumn<Medicine, String> nameColumn;
    @FXML
    private TableColumn<Medicine, String> categoryColumn;
    @FXML
    private TableColumn<Medicine, String> specificationColumn;
    @FXML
    private TableColumn<Medicine, Double> priceColumn;
    @FXML
    private TableColumn<Medicine, Integer> stockColumn;
    @FXML
    private TableColumn<Medicine, String> statusColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField specificationField;
    @FXML
    private TextField manufacturerField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private DatePicker productionDatePicker;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private TextArea usageArea;
    @FXML
    private TextArea contraindicationArea;

    private ObservableList<Medicine> medicineList;
    private DialogManager dialogManager;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        initializeTable();
        loadSampleData();
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        specificationColumn.setCellValueFactory(new PropertyValueFactory<>("specification"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        medicineList = FXCollections.observableArrayList();
        medicineTable.setItems(medicineList);
    }

    private void loadSampleData() {
        Medicine med1 = new Medicine("M001", "阿莫西林胶囊", "抗生素", 25.5, 100);
        med1.setSpecification("0.5g*24粒");
        med1.setManufacturer("某制药厂");
        medicineList.add(med1);

        Medicine med2 = new Medicine("M002", "布洛芬缓释胶囊", "解热镇痛药", 18.0, 50);
        med2.setSpecification("0.3g*20粒");
        med2.setManufacturer("某制药厂");
        medicineList.add(med2);

        Medicine med3 = new Medicine("M003", "维生素C片", "维生素", 12.5, 200);
        med3.setSpecification("100mg*100片");
        med3.setManufacturer("某制药厂");
        medicineList.add(med3);
    }

    @FXML
    private void handleAddMedicine() {
        try {
            Medicine medicine = new Medicine();
            medicine.setId("M" + String.format("%03d", medicineList.size() + 1));
            medicine.setName(nameField.getText());
            medicine.setCategory(categoryField.getText());
            medicine.setSpecification(specificationField.getText());
            medicine.setManufacturer(manufacturerField.getText());
            medicine.setPrice(priceField.getText() != null && !priceField.getText().isEmpty() ? Double.parseDouble(priceField.getText()) : 0.0);
            medicine.setStockQuantity(stockField.getText() != null && !stockField.getText().isEmpty() ? Integer.parseInt(stockField.getText()) : 0);
            medicine.setProductionDate(productionDatePicker.getValue());
            medicine.setExpiryDate(expiryDatePicker.getValue());
            medicine.setUsage(usageArea.getText());
            medicine.setContraindication(contraindicationArea.getText());

            medicineList.add(medicine);
            clearForm();
            dialogManager.showInfo("药品信息添加成功！");
        } catch (Exception e) {
            dialogManager.showError(500, "添加失败：" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateMedicine() {
        Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
        if (selectedMedicine == null) {
            dialogManager.showWarning("请先选择要修改的药品");
            return;
        }

        selectedMedicine.setName(nameField.getText());
        selectedMedicine.setCategory(categoryField.getText());
        selectedMedicine.setSpecification(specificationField.getText());
        selectedMedicine.setManufacturer(manufacturerField.getText());
        selectedMedicine.setPrice(priceField.getText() != null && !priceField.getText().isEmpty() ? Double.parseDouble(priceField.getText()) : 0.0);
        selectedMedicine.setStockQuantity(stockField.getText() != null && !stockField.getText().isEmpty() ? Integer.parseInt(stockField.getText()) : 0);
        selectedMedicine.setProductionDate(productionDatePicker.getValue());
        selectedMedicine.setExpiryDate(expiryDatePicker.getValue());
        selectedMedicine.setUsage(usageArea.getText());
        selectedMedicine.setContraindication(contraindicationArea.getText());

        medicineTable.refresh();
        dialogManager.showInfo("药品信息修改成功！");
    }

    @FXML
    private void handleDeleteMedicine() {
        Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
        if (selectedMedicine == null) {
            dialogManager.showWarning("请先选择要删除的药品");
            return;
        }

        if (dialogManager.showConfirm("确认", "确定要删除该药品吗？")) {
            medicineList.remove(selectedMedicine);
            clearForm();
            dialogManager.showInfo("药品删除成功！");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            medicineTable.setItems(medicineList);
            return;
        }

        ObservableList<Medicine> filteredList = FXCollections.observableArrayList();
        for (Medicine medicine : medicineList) {
            if ((medicine.getName() != null && medicine.getName().toLowerCase().contains(keyword)) ||
                (medicine.getCategory() != null && medicine.getCategory().toLowerCase().contains(keyword)) ||
                (medicine.getId() != null && medicine.getId().toLowerCase().contains(keyword))) {
                filteredList.add(medicine);
            }
        }
        medicineTable.setItems(filteredList);
    }

    @FXML
    private void handleTableClick() {
        Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
        if (selectedMedicine != null) {
            nameField.setText(selectedMedicine.getName());
            categoryField.setText(selectedMedicine.getCategory());
            specificationField.setText(selectedMedicine.getSpecification());
            manufacturerField.setText(selectedMedicine.getManufacturer());
            priceField.setText(selectedMedicine.getPrice() > 0 ? String.valueOf(selectedMedicine.getPrice()) : "");
            stockField.setText(selectedMedicine.getStockQuantity() > 0 ? String.valueOf(selectedMedicine.getStockQuantity()) : "");
            productionDatePicker.setValue(selectedMedicine.getProductionDate());
            expiryDatePicker.setValue(selectedMedicine.getExpiryDate());
            usageArea.setText(selectedMedicine.getUsage());
            contraindicationArea.setText(selectedMedicine.getContraindication());
        }
    }

    @FXML
    private void handleBack() {
        App.navigateTo(AppPage.HOME);
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        specificationField.clear();
        manufacturerField.clear();
        priceField.clear();
        stockField.clear();
        productionDatePicker.setValue(null);
        expiryDatePicker.setValue(null);
        usageArea.clear();
        contraindicationArea.clear();
        medicineTable.getSelectionModel().clearSelection();
    }
}
