package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class PatientResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfoResponse {
        private String id;
        private String name;
        private String gender;
        private Integer age;
        private String phone;
        private String idCard;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private LocalDate registrationDate;
        private List<String> medicalHistory;
        private String status;
    }
}
