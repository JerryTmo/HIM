package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DoctorResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfoResponse {
        private String id;
        private String name;
        private String department;
        private String title;
        private String phone;
        private String specialty;
        private String description;
        private List<String> availableTimes;
        private String status;
    }
}
