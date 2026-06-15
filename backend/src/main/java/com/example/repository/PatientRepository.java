package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.PatientEntity;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, String> {

    List<PatientEntity> findByStatus(String status);

    @Query("SELECT p FROM PatientEntity p WHERE p.name LIKE %:name%")
    List<PatientEntity> searchByName(String name);
}
