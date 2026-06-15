package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

public class DoctorRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建医生")
    public static class CreateDoctorRequest {

        @NotBlank(message = "医生姓名不能为空")
        @Size(max = 50, message = "姓名长度不能超过50个字符")
        @Schema(description = "姓名", requiredMode = RequiredMode.REQUIRED, example = "王医生")
        private String name;

        @Size(max = 50, message = "科室长度不能超过50个字符")
        @Schema(description = "科室", example = "内科")
        private String department;

        @Size(max = 50, message = "职称长度不能超过50个字符")
        @Schema(description = "职称", example = "主任医师")
        private String title;

        @Size(max = 20, message = "电话长度不能超过20个字符")
        @Schema(description = "电话", example = "13800138000")
        private String phone;

        @Size(max = 100, message = "专长长度不能超过100个字符")
        @Schema(description = "专长", example = "心血管疾病")
        private String specialty;

        @Size(max = 500, message = "描述长度不能超过500个字符")
        @Schema(description = "描述", example = "从医20年，经验丰富")
        private String description;

        @Schema(description = "可预约时间列表")
        private List<String> availableTimes;

        @Schema(description = "状态", example = "在职")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新医生")
    public static class UpdateDoctorRequest {

        @NotBlank(message = "医生ID不能为空")
        @Schema(description = "医生ID", requiredMode = RequiredMode.REQUIRED)
        private String id;

        @Size(max = 50, message = "姓名长度不能超过50个字符")
        @Schema(description = "姓名", example = "王医生")
        private String name;

        @Size(max = 50, message = "科室长度不能超过50个字符")
        @Schema(description = "科室", example = "内科")
        private String department;

        @Size(max = 50, message = "职称长度不能超过50个字符")
        @Schema(description = "职称", example = "主任医师")
        private String title;

        @Size(max = 20, message = "电话长度不能超过20个字符")
        @Schema(description = "电话", example = "13800138000")
        private String phone;

        @Size(max = 100, message = "专长长度不能超过100个字符")
        @Schema(description = "专长", example = "心血管疾病")
        private String specialty;

        @Size(max = 500, message = "描述长度不能超过500个字符")
        @Schema(description = "描述", example = "从医20年，经验丰富")
        private String description;

        @Schema(description = "可预约时间列表")
        private List<String> availableTimes;

        @Schema(description = "状态", example = "在职")
        private String status;
    }
}
