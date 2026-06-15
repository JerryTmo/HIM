package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.DoctorEntity;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {

    List<DoctorEntity> findByDepartment(String department);

    List<DoctorEntity> findByStatus(String status);

    @Query("SELECT d FROM DoctorEntity d WHERE d.name LIKE %:name%")
    List<DoctorEntity> searchByName(String name);
}
