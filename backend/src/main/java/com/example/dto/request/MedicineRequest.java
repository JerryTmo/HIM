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

public class MedicineRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "创建药品")
    public static class CreateMedicineRequest {

        @NotBlank(message = "药品名称不能为空")
        @Size(max = 100, message = "药品名称长度不能超过100个字符")
        @Schema(description = "药品名称", requiredMode = RequiredMode.REQUIRED, example = "阿莫西林")
        private String name;

        @Size(max = 50, message = "类别长度不能超过50个字符")
        @Schema(description = "类别", example = "抗生素")
        private String category;

        @Size(max = 100, message = "规格长度不能超过100个字符")
        @Schema(description = "规格", example = "0.5g*12片")
        private String specification;

        @Size(max = 100, message = "生产厂商长度不能超过100个字符")
        @Schema(description = "生产厂商", example = "华北制药")
        private String manufacturer;

        @Schema(description = "价格", example = "25.5")
        private Double price;

        @Schema(description = "库存数量", example = "100")
        private Integer stockQuantity;

        @Schema(description = "生产日期")
        private LocalDate productionDate;

        @Schema(description = "有效期至")
        private LocalDate expiryDate;

        @Size(max = 500, message = "用法长度不能超过500个字符")
        @Schema(description = "用法", example = "每日3次，每次1片")
        private String usage;

        @Size(max = 500, message = "禁忌症长度不能超过500个字符")
        @Schema(description = "禁忌症", example = "青霉素过敏者禁用")
        private String contraindication;

        @Schema(description = "状态", example = "正常")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新药品")
    public static class UpdateMedicineRequest {

        @NotBlank(message = "药品ID不能为空")
        @Schema(description = "药品ID", requiredMode = RequiredMode.REQUIRED)
        private String id;

        @Size(max = 100, message = "药品名称长度不能超过100个字符")
        @Schema(description = "药品名称", example = "阿莫西林")
        private String name;

        @Size(max = 50, message = "类别长度不能超过50个字符")
        @Schema(description = "类别", example = "抗生素")
        private String category;

        @Size(max = 100, message = "规格长度不能超过100个字符")
        @Schema(description = "规格", example = "0.5g*12片")
        private String specification;

        @Size(max = 100, message = "生产厂商长度不能超过100个字符")
        @Schema(description = "生产厂商", example = "华北制药")
        private String manufacturer;

        @Schema(description = "价格", example = "25.5")
        private Double price;

        @Schema(description = "库存数量", example = "100")
        private Integer stockQuantity;

        @Schema(description = "生产日期")
        private LocalDate productionDate;

        @Schema(description = "有效期至")
        private LocalDate expiryDate;

        @Size(max = 500, message = "用法长度不能超过500个字符")
        @Schema(description = "用法", example = "每日3次，每次1片")
        private String usage;

        @Size(max = 500, message = "禁忌症长度不能超过500个字符")
        @Schema(description = "禁忌症", example = "青霉素过敏者禁用")
        private String contraindication;

        @Schema(description = "状态", example = "正常")
        private String status;
    }
}
