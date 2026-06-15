package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "patient_id", length = 36, nullable = false)
    private String patientId;

    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Column(name = "doctor_id", length = 36, nullable = false)
    private String doctorId;

    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "chief_complaint", length = 500)
    private String chiefComplaint;

    @Column(name = "diagnosis", length = 500)
    private String diagnosis;

    @Column(name = "treatment_plan", length = 1000)
    private String treatmentPlan;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "record_prescriptions", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "prescription", length = 500)
    private List<String> prescriptions = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "record_examinations", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "examination", length = 500)
    private List<String> examinationRecords = new ArrayList<>();

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (visitDate == null) {
            visitDate = LocalDate.now();
        }
        if (status == null) {
            status = "已完成";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecordEntity that = (MedicalRecordEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode();
    }
}
