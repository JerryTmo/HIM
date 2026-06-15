package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建病历")
    public static class CreateMedicalRecordRequest {

        @NotBlank(message = "患者ID不能为空")
        @Schema(description = "患者ID", requiredMode = RequiredMode.REQUIRED)
        private String patientId;

        @Schema(description = "患者姓名")
        private String patientName;

        @NotBlank(message = "医生ID不能为空")
        @Schema(description = "医生ID", requiredMode = RequiredMode.REQUIRED)
        private String doctorId;

        @Schema(description = "医生姓名")
        private String doctorName;

        @Schema(description = "科室")
        private String department;

        @Schema(description = "就诊日期")
        private LocalDate visitDate;

        @Size(max = 500, message = "主诉长度不能超过500个字符")
        @Schema(description = "主诉", example = "头痛三天")
        private String chiefComplaint;

        @Size(max = 500, message = "诊断长度不能超过500个字符")
        @Schema(description = "诊断", example = "感冒")
        private String diagnosis;

        @Size(max = 1000, message = "治疗方案长度不能超过1000个字符")
        @Schema(description = "治疗方案", example = "开药、休息")
        private String treatmentPlan;

        @Schema(description = "处方列表")
        private List<String> prescriptions;

        @Schema(description = "检查记录列表")
        private List<String> examinationRecords;

        @Size(max = 500, message = "备注长度不能超过500个字符")
        @Schema(description = "备注")
        private String notes;

        @Schema(description = "状态", example = "已完成")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新病历")
    public static class UpdateMedicalRecordRequest {

        @NotBlank(message = "病历ID不能为空")
        @Schema(description = "病历ID", requiredMode = RequiredMode.REQUIRED)
        private String id;

        @Schema(description = "患者ID")
        private String patientId;

        @Schema(description = "患者姓名")
        private String patientName;

        @Schema(description = "医生ID")
        private String doctorId;

        @Schema(description = "医生姓名")
        private String doctorName;

        @Schema(description = "科室")
        private String department;

        @Schema(description = "就诊日期")
        private LocalDate visitDate;

        @Size(max = 500, message = "主诉长度不能超过500个字符")
        @Schema(description = "主诉", example = "头痛三天")
        private String chiefComplaint;

        @Size(max = 500, message = "诊断长度不能超过500个字符")
        @Schema(description = "诊断", example = "感冒")
        private String diagnosis;

        @Size(max = 1000, message = "治疗方案长度不能超过1000个字符")
        @Schema(description = "治疗方案", example = "开药、休息")
        private String treatmentPlan;

        @Schema(description = "处方列表")
        private List<String> prescriptions;

        @Schema(description = "检查记录列表")
        private List<String> examinationRecords;

        @Size(max = 500, message = "备注长度不能超过500个字符")
        @Schema(description = "备注")
        private String notes;

        @Schema(description = "状态", example = "已完成")
        private String status;
    }
}
