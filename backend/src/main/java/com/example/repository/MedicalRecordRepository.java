package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.MedicalRecordEntity;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, String> {

    List<MedicalRecordEntity> findByPatientId(String patientId);

    List<MedicalRecordEntity> findByDoctorId(String doctorId);

    List<MedicalRecordEntity> findByStatus(String status);
}
