package com.example.controller;

import com.example.annotation.RequirePermission;
import com.example.annotation.RequireRole;
import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
import com.example.dto.response.MenuResponse.MenuDTO;
import com.example.service.MenuService;
import com.example.util.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserUtils userUtils;

    @GetMapping("/findByMenu")
    @Operation(summary = "获取当前用户菜单树")
    public ServiceResult<List<MenuDTO>> findByMenu() {
        return menuService.findByMenu(userUtils.getUserDetails().getUsername());
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有菜单树（管理端）")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission("menu:read")
    public ServiceResult<List<MenuDTO>> findAll() {
        return menuService.findAllMenus();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission("menu:read")
    public ServiceResult<MenuDTO> getById(@PathVariable String id) {
        return menuService.getMenuById(id);
    }

    @PostMapping("/insertMenus")
    @Operation(summary = "创建菜单")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission("menu:create")
    public ServiceResult<String> create(@Valid @RequestBody MenuRequest request) {
        return menuService.createMenu(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission("menu:update")
    public ServiceResult<Void> update(@PathVariable String id, @Valid @RequestBody MenuRequest request) {
        return menuService.updateMenu(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @RequireRole({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    @RequirePermission("menu:delete")
    public ServiceResult<Void> delete(@PathVariable String id) {
        return menuService.deleteMenu(id);
    }
}
