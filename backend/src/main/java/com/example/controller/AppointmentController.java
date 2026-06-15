package com.example.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.AppointmentRequest.CreateAppointmentRequest;
import com.example.dto.request.AppointmentRequest.UpdateAppointmentRequest;
import com.example.dto.response.AppointmentResponse.AppointmentInfoResponse;
import com.example.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "预约管理")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "创建预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"appointment:create"})
    public ServiceResult<String> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    @PutMapping
    @Operation(summary = "更新预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR"})
    @RequirePermission({"appointment:update"})
    public ServiceResult<Void> updateAppointment(@Valid @RequestBody UpdateAppointmentRequest request) {
        return appointmentService.updateAppointment(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"appointment:delete"})
    public ServiceResult<Void> deleteAppointment(@PathVariable String id) {
        return appointmentService.deleteAppointment(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"appointment:read"})
    public ServiceResult<AppointmentInfoResponse> getAppointmentById(@PathVariable String id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping
    @Operation(summary = "获取所有预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"appointment:read"})
    public ServiceResult<List<AppointmentInfoResponse>> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "根据患者获取预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"appointment:read"})
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByPatient(@PathVariable String patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "根据医生获取预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"appointment:read"})
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDoctor(@PathVariable String doctorId) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

    @GetMapping("/date")
    @Operation(summary = "根据日期获取预约")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"appointment:read"})
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAppointmentsByDate(date);
    }
}
