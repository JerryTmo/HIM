package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalRecordInfoResponse {
        private String id;
        private String patientId;
        private String patientName;
        private String doctorId;
        private String doctorName;
        private String department;
        private LocalDate visitDate;
        private String chiefComplaint;
        private String diagnosis;
        private String treatmentPlan;
        private List<String> prescriptions;
        private List<String> examinationRecords;
        private String notes;
        private String status;
        private LocalDateTime createdAt;
    }
}
