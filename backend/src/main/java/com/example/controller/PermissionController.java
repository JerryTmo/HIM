package com.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ServiceResult;
import com.example.dto.request.PermissionRequest.UpdateCodeRequest;
import com.example.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "權限模塊")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/updateCode")
    @Operation(summary = "更新權限")
    public ServiceResult<Integer> updateCode(@Valid @RequestBody UpdateCodeRequest updateCodeRequest) {
        return permissionService.updateCode(updateCodeRequest);
    }

}
