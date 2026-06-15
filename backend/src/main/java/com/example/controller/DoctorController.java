package com.example.controller;

import org.springframework.web.bind.annotation.*;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.DoctorRequest.CreateDoctorRequest;
import com.example.dto.request.DoctorRequest.UpdateDoctorRequest;
import com.example.dto.response.DoctorResponse.DoctorInfoResponse;
import com.example.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "医生管理")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @Operation(summary = "创建医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"doctor:create"})
    public ServiceResult<String> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @PutMapping
    @Operation(summary = "更新医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"doctor:update"})
    public ServiceResult<Void> updateDoctor(@Valid @RequestBody UpdateDoctorRequest request) {
        return doctorService.updateDoctor(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"doctor:delete"})
    public ServiceResult<Void> deleteDoctor(@PathVariable String id) {
        return doctorService.deleteDoctor(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"doctor:read"})
    public ServiceResult<DoctorInfoResponse> getDoctorById(@PathVariable String id) {
        return doctorService.getDoctorById(id);
    }

    @GetMapping
    @Operation(summary = "获取所有医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"doctor:read"})
    public ServiceResult<List<DoctorInfoResponse>> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "根据科室获取医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"doctor:read"})
    public ServiceResult<List<DoctorInfoResponse>> getDoctorsByDepartment(@PathVariable String department) {
        return doctorService.getDoctorsByDepartment(department);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索医生")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"doctor:read"})
    public ServiceResult<List<DoctorInfoResponse>> searchDoctors(@RequestParam String name) {
        return doctorService.searchDoctors(name);
    }
}
