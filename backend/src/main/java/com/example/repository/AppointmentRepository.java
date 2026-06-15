package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.AppointmentEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {

    List<AppointmentEntity> findByPatientId(String patientId);

    List<AppointmentEntity> findByDoctorId(String doctorId);

    List<AppointmentEntity> findByStatus(String status);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDate = :date")
    List<AppointmentEntity> findByDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date")
    List<AppointmentEntity> findByDoctorAndDate(@Param("doctorId") String doctorId, @Param("date") LocalDate date);
}
