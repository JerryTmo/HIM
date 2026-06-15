package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentInfoResponse {
        private String id;
        private String patientId;
        private String patientName;
        private String doctorId;
        private String doctorName;
        private String department;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String appointmentType;
        private String symptoms;
        private String status;
        private String notes;
        private LocalDateTime createdAt;
    }
}
