package com.example.controller;

import org.springframework.web.bind.annotation.*;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.MedicineRequest.CreateMedicineRequest;
import com.example.dto.request.MedicineRequest.UpdateMedicineRequest;
import com.example.dto.response.MedicineResponse.MedicineInfoResponse;
import com.example.service.MedicineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@Tag(name = "药品管理")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    @Operation(summary = "创建药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"medicine:create"})
    public ServiceResult<String> createMedicine(@Valid @RequestBody CreateMedicineRequest request) {
        return medicineService.createMedicine(request);
    }

    @PutMapping
    @Operation(summary = "更新药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"medicine:update"})
    public ServiceResult<Void> updateMedicine(@Valid @RequestBody UpdateMedicineRequest request) {
        return medicineService.updateMedicine(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission({"medicine:delete"})
    public ServiceResult<Void> deleteMedicine(@PathVariable String id) {
        return medicineService.deleteMedicine(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medicine:read"})
    public ServiceResult<MedicineInfoResponse> getMedicineById(@PathVariable String id) {
        return medicineService.getMedicineById(id);
    }

    @GetMapping
    @Operation(summary = "获取所有药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medicine:read"})
    public ServiceResult<List<MedicineInfoResponse>> getAllMedicines() {
        return medicineService.getAllMedicines();
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据类别获取药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medicine:read"})
    public ServiceResult<List<MedicineInfoResponse>> getMedicinesByCategory(@PathVariable String category) {
        return medicineService.getMedicinesByCategory(category);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索药品")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_DOCTOR", "ROLE_USER"})
    @RequirePermission({"medicine:read"})
    public ServiceResult<List<MedicineInfoResponse>> searchMedicines(@RequestParam String name) {
        return medicineService.searchMedicines(name);
    }
}
