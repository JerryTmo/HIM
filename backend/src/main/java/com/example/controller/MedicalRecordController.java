package com.example.controller;

import org.springframework.web.bind.annotation.*;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.example.dto.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.example.dto.response.MedicalRecordResponse.MedicalRecordInfoResponse;
import com.example.service.MedicalRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "病历管理")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @Operation(summary = "创建病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"medical:create"})
    public ServiceResult<String> createMedicalRecord(@Valid @RequestBody CreateMedicalRecordRequest request) {
        return medicalRecordService.createMedicalRecord(request);
    }

    @PutMapping
    @Operation(summary = "更新病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"medical:update"})
    public ServiceResult<Void> updateMedicalRecord(@Valid @RequestBody UpdateMedicalRecordRequest request) {
        return medicalRecordService.updateMedicalRecord(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"medical:delete"})
    public ServiceResult<Void> deleteMedicalRecord(@PathVariable String id) {
        return medicalRecordService.deleteMedicalRecord(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medical:read"})
    public ServiceResult<MedicalRecordInfoResponse> getMedicalRecordById(@PathVariable String id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @GetMapping
    @Operation(summary = "获取所有病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medical:read"})
    public ServiceResult<List<MedicalRecordInfoResponse>> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "根据患者获取病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medical:read"})
    public ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByPatient(@PathVariable String patientId) {
        return medicalRecordService.getMedicalRecordsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "根据医生获取病历")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medical:read"})
    public ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByDoctor(@PathVariable String doctorId) {
        return medicalRecordService.getMedicalRecordsByDoctor(doctorId);
    }
}
