package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建预约")
    public static class CreateAppointmentRequest {

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

        @NotNull(message = "预约日期不能为空")
        @Schema(description = "预约日期", requiredMode = RequiredMode.REQUIRED)
        private LocalDate appointmentDate;

        @NotNull(message = "预约时间不能为空")
        @Schema(description = "预约时间", requiredMode = RequiredMode.REQUIRED)
        private LocalTime appointmentTime;

        @Size(max = 50, message = "预约类型长度不能超过50个字符")
        @Schema(description = "预约类型", example = "初诊")
        private String appointmentType;

        @Size(max = 500, message = "症状描述长度不能超过500个字符")
        @Schema(description = "症状描述", example = "头痛、发热")
        private String symptoms;

        @Schema(description = "状态", example = "待就诊")
        private String status;

        @Size(max = 500, message = "备注长度不能超过500个字符")
        @Schema(description = "备注")
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新预约")
    public static class UpdateAppointmentRequest {

        @NotBlank(message = "预约ID不能为空")
        @Schema(description = "预约ID", requiredMode = RequiredMode.REQUIRED)
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

        @Schema(description = "预约日期")
        private LocalDate appointmentDate;

        @Schema(description = "预约时间")
        private LocalTime appointmentTime;

        @Size(max = 50, message = "预约类型长度不能超过50个字符")
        @Schema(description = "预约类型", example = "初诊")
        private String appointmentType;

        @Size(max = 500, message = "症状描述长度不能超过500个字符")
        @Schema(description = "症状描述", example = "头痛、发热")
        private String symptoms;

        @Schema(description = "状态", example = "待就诊")
        private String status;

        @Size(max = 500, message = "备注长度不能超过500个字符")
        @Schema(description = "备注")
        private String notes;
    }
}
