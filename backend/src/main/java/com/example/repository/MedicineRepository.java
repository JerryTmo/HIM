package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.MedicineEntity;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, String> {

    List<MedicineEntity> findByCategory(String category);

    List<MedicineEntity> findByStatus(String status);

    @Query("SELECT m FROM MedicineEntity m WHERE m.name LIKE %:name%")
    List<MedicineEntity> searchByName(String name);
}
