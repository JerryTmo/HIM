package com.example.menu;

import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public enum AppPage {

    // 頁面枚舉常量
    LOGIN("login", 1000, 550, Color.TRANSPARENT, StageStyle.DECORATED, true, "医院医疗系统登录"),
    HOME("home", 1300, 700, Color.WHITE, StageStyle.DECORATED, false, "医院医疗系统"),
    REGISTER("register", 450, 600, Color.TRANSPARENT, StageStyle.TRANSPARENT, true, "註冊頁面"),
    PATIENT_MANAGEMENT("patient-management", 1200, 650, Color.WHITE, StageStyle.DECORATED, false, "患者管理"),
    DOCTOR_SCHEDULE("doctor-schedule", 1200, 650, Color.WHITE, StageStyle.DECORATED, false, "医生排班"),
    MEDICAL_RECORD("medical-record", 1200, 650, Color.WHITE, StageStyle.DECORATED, false, "病历管理"),
    MEDICINE_MANAGEMENT("medicine-management", 1200, 650, Color.WHITE, StageStyle.DECORATED, false, "药品管理"),
    APPOINTMENT_MANAGEMENT("appointment-management", 1200, 650, Color.WHITE, StageStyle.DECORATED, false, "预约管理"),
    SETTINGS("settings", 800, 550, Color.WHITE, StageStyle.DECORATED, false, "設置頁面");

    // 所有欄位都是 final，在建構子中設定後就不能再修改
    private final String fxmlName;
    private final int width;
    private final int height;
    private final Color fillColor;
    private final StageStyle stageStyle;
    private final boolean draggable;
    private final String description;

    /**
     * 私有建構子，枚舉的建構子只能是私有的
     */
    AppPage(String fxmlName, int width, int height, Color fillColor,
            StageStyle stageStyle, boolean draggable, String description) {
        this.fxmlName = fxmlName;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.stageStyle = stageStyle;
        this.draggable = draggable;
        this.description = description;
    }

    // ========== Getter 方法 ==========
    public String getFxmlName() {
        return fxmlName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public StageStyle getStageStyle() {
        return stageStyle;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public String getDescription() {
        return description;
    }

    // ========== 實用工具方法 ==========

    /**
     * 根據FXML名稱查找對應的頁面枚舉
     */
    public static AppPage fromFxml(String fxmlName) {
        for (AppPage page : AppPage.values()) {
            if (page.fxmlName.equals(fxmlName)) {
                return page;
            }
        }
        throw new IllegalArgumentException("找不到對應的頁面: " + fxmlName);
    }

    /**
     * 獲取完整的FXML文件路徑（用於加載資源）
     */
    public String getFxmlPath() {
        return "/com/example/view/" + fxmlName + ".fxml";
    }

    @Override
    public String toString() {
        return String.format("AppPage{name='%s', width=%d, height=%d, description='%s'}",
                this.name(), width, height, description);
    }
}