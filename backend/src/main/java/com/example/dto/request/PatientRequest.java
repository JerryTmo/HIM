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

public class PatientRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建患者")
    public static class CreatePatientRequest {

        @NotBlank(message = "患者姓名不能为空")
        @Size(max = 50, message = "姓名长度不能超过50个字符")
        @Schema(description = "姓名", requiredMode = RequiredMode.REQUIRED, example = "张三")
        private String name;

        @Schema(description = "性别", example = "男")
        private String gender;

        @Schema(description = "年龄", example = "30")
        private Integer age;

        @Size(max = 20, message = "电话长度不能超过20个字符")
        @Schema(description = "电话", example = "13800138000")
        private String phone;

        @Size(max = 18, message = "身份证号长度不能超过18个字符")
        @Schema(description = "身份证号", example = "110101199001011234")
        private String idCard;

        @Size(max = 200, message = "地址长度不能超过200个字符")
        @Schema(description = "地址", example = "北京市朝阳区")
        private String address;

        @Size(max = 50, message = "紧急联系人长度不能超过50个字符")
        @Schema(description = "紧急联系人", example = "李四")
        private String emergencyContact;

        @Size(max = 20, message = "紧急联系电话长度不能超过20个字符")
        @Schema(description = "紧急联系电话", example = "13900139000")
        private String emergencyPhone;

        @Schema(description = "登记日期")
        private LocalDate registrationDate;

        @Schema(description = "病史列表")
        private List<String> medicalHistory;

        @Schema(description = "状态", example = "正常")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新患者")
    public static class UpdatePatientRequest {

        @NotBlank(message = "患者ID不能为空")
        @Schema(description = "患者ID", requiredMode = RequiredMode.REQUIRED)
        private String id;

        @Size(max = 50, message = "姓名长度不能超过50个字符")
        @Schema(description = "姓名", example = "张三")
        private String name;

        @Schema(description = "性别", example = "男")
        private String gender;

        @Schema(description = "年龄", example = "30")
        private Integer age;

        @Size(max = 20, message = "电话长度不能超过20个字符")
        @Schema(description = "电话", example = "13800138000")
        private String phone;

        @Size(max = 18, message = "身份证号长度不能超过18个字符")
        @Schema(description = "身份证号", example = "110101199001011234")
        private String idCard;

        @Size(max = 200, message = "地址长度不能超过200个字符")
        @Schema(description = "地址", example = "北京市朝阳区")
        private String address;

        @Size(max = 50, message = "紧急联系人长度不能超过50个字符")
        @Schema(description = "紧急联系人", example = "李四")
        private String emergencyContact;

        @Size(max = 20, message = "紧急联系电话长度不能超过20个字符")
        @Schema(description = "紧急联系电话", example = "13900139000")
        private String emergencyPhone;

        @Schema(description = "登记日期")
        private LocalDate registrationDate;

        @Schema(description = "病史列表")
        private List<String> medicalHistory;

        @Schema(description = "状态", example = "正常")
        private String status;
    }
}
