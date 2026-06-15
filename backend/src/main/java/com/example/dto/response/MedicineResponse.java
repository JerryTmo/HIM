package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MedicineResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineInfoResponse {
        private String id;
        private String name;
        private String category;
        private String specification;
        private String manufacturer;
        private Double price;
        private Integer stockQuantity;
        private LocalDate productionDate;
        private LocalDate expiryDate;
        private String usage;
        private String contraindication;
        private String status;
    }
}
