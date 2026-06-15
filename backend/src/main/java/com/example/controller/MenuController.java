package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
import com.example.service.MenuService;
import com.example.service.impl.MenuServiceImpl.MenuDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "菜單模塊")
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/findByMenu")
    @Operation(summary = "查詢菜單")
    public ServiceResult<List<MenuDTO>> findByMenu() {
        return menuService.findByMenu("admin");
    }

    @PostMapping("/insertMenus")
    @Operation(summary = "註冊菜單")
    public ServiceResult<Integer> insertMenus(@Valid @RequestBody MenuRequest menuRequest) {
        return menuService.insertMenus(menuRequest);
    }
}
