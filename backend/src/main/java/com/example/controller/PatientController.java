package com.example.controller;

import org.springframework.web.bind.annotation.*;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.PatientRequest.CreatePatientRequest;
import com.example.dto.request.PatientRequest.UpdatePatientRequest;
import com.example.dto.response.PatientResponse.PatientInfoResponse;
import com.example.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "患者管理")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "创建患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"patient:create"})
    public ServiceResult<String> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return patientService.createPatient(request);
    }

    @PutMapping
    @Operation(summary = "更新患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"patient:update"})
    public ServiceResult<Void> updatePatient(@Valid @RequestBody UpdatePatientRequest request) {
        return patientService.updatePatient(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"patient:delete"})
    public ServiceResult<Void> deletePatient(@PathVariable String id) {
        return patientService.deletePatient(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"patient:read"})
    public ServiceResult<PatientInfoResponse> getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id);
    }

    @GetMapping
    @Operation(summary = "获取所有患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"patient:read"})
    public ServiceResult<List<PatientInfoResponse>> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索患者")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"patient:read"})
    public ServiceResult<List<PatientInfoResponse>> searchPatients(@RequestParam String name) {
        return patientService.searchPatients(name);
    }
}
