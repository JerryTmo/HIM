package com.example.config;

import com.example.entity.MenuEntity;
import com.example.entity.PermissionEntity;
import com.example.entity.RoleEntity;
import com.example.entity.UserEntity;
import com.example.repository.MenuRepository;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) {
        initializePermissions();
        initializeRoles();
        initializeAdminUser();
        assignPermissionsToRoles();
        initializeMenus();
    }

    private void initializePermissions() {
        if (permissionRepository.count() > 0) {
            log.info("权限数据已存在，跳过初始化");
            return;
        }

        List<PermissionEntity> permissions = List.of(
                // 用户管理权限
                createPermission("查看用户", "user:read", "用户管理", "查看用户信息的权限"),
                createPermission("创建用户", "user:create", "用户管理", "创建新用户的权限"),
                createPermission("更新用户", "user:update", "用户管理", "更新用户信息的权限"),
                createPermission("删除用户", "user:delete", "用户管理", "删除用户的权限"),
                createPermission("分配用户角色", "user:role:assign", "用户管理", "为用户分配角色的权限"),
                createPermission("查看用户角色", "user:role:read", "用户管理", "查看用户角色的权限"),

                // 角色管理权限
                createPermission("查看角色", "role:read", "角色管理", "查看角色信息的权限"),
                createPermission("创建角色", "role:create", "角色管理", "创建新角色的权限"),
                createPermission("更新角色", "role:update", "角色管理", "更新角色信息的权限"),
                createPermission("删除角色", "role:delete", "角色管理", "删除角色的权限"),
                createPermission("分配角色权限", "role:assign", "角色管理", "为角色分配权限的权限"),

                // 权限管理权限
                createPermission("查看权限", "permission:read", "权限管理", "查看权限信息的权限"),
                createPermission("创建权限", "permission:create", "权限管理", "创建新权限的权限"),
                createPermission("更新权限", "permission:update", "权限管理", "更新权限信息的权限"),
                createPermission("删除权限", "permission:delete", "权限管理", "删除权限的权限"),

                // 患者管理权限
                createPermission("查看患者", "patient:read", "患者管理", "查看患者信息的权限"),
                createPermission("创建患者", "patient:create", "患者管理", "创建新患者的权限"),
                createPermission("更新患者", "patient:update", "患者管理", "更新患者信息的权限"),
                createPermission("删除患者", "patient:delete", "患者管理", "删除患者的权限"),

                // 医生管理权限
                createPermission("查看医生", "doctor:read", "医生管理", "查看医生信息的权限"),
                createPermission("创建医生", "doctor:create", "医生管理", "创建新医生的权限"),
                createPermission("更新医生", "doctor:update", "医生管理", "更新医生信息的权限"),
                createPermission("删除医生", "doctor:delete", "医生管理", "删除医生的权限"),

                // 预约管理权限
                createPermission("查看预约", "appointment:read", "预约管理", "查看预约信息的权限"),
                createPermission("创建预约", "appointment:create", "预约管理", "创建新预约的权限"),
                createPermission("更新预约", "appointment:update", "预约管理", "更新预约信息的权限"),
                createPermission("删除预约", "appointment:delete", "预约管理", "删除预约的权限"),

                // 病历管理权限
                createPermission("查看病历", "medical:read", "病历管理", "查看病历信息的权限"),
                createPermission("创建病历", "medical:create", "病历管理", "创建新病历的权限"),
                createPermission("更新病历", "medical:update", "病历管理", "更新病历信息的权限"),
                createPermission("删除病历", "medical:delete", "病历管理", "删除病历的权限"),

                // 药品管理权限
                createPermission("查看药品", "medicine:read", "药品管理", "查看药品信息的权限"),
                createPermission("创建药品", "medicine:create", "药品管理", "创建新药品的权限"),
                createPermission("更新药品", "medicine:update", "药品管理", "更新药品信息的权限"),
                createPermission("删除药品", "medicine:delete", "药品管理", "删除药品的权限"),

                // 菜单管理权限
                createPermission("查看菜单", "menu:read", "菜单管理", "查看菜单信息的权限"),
                createPermission("创建菜单", "menu:create", "菜单管理", "创建新菜单的权限"),
                createPermission("更新菜单", "menu:update", "菜单管理", "更新菜单信息的权限"),
                createPermission("删除菜单", "menu:delete", "菜单管理", "删除菜单的权限"),

                // 系统管理权限
                createPermission("查看系统设置", "system:read", "系统管理", "查看系统设置的权限"),
                createPermission("更新系统设置", "system:update", "系统管理", "更新系统设置的权限"),

                // AI 助手权限
                createPermission("AI 对话", "ai:chat", "AI助手", "使用AI对话功能的权限"),
                createPermission("AI 思维导图", "ai:mindmap", "AI助手", "使用AI生成思维导图的权限"),
                createPermission("AI 智能导诊", "ai:triage", "AI助手", "使用AI智能导诊的权限")
        );

        permissionRepository.saveAll(permissions);
        log.info("初始化权限数据成功，共创建 {} 个权限", permissions.size());
    }

    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        List<RoleEntity> roles = List.of(
                createRole("超级管理员", "ROLE_SUPER_ADMIN", "拥有系统所有权限的超级管理员角色", false),
                createRole("系统管理员", "ROLE_ADMIN", "系统管理员角色，拥有大部分管理权限", false),
                createRole("医生", "ROLE_DOCTOR", "医生角色，拥有医疗相关操作权限", false),
                createRole("普通用户", "ROLE_USER", "普通用户角色，仅有基础查看权限", true)
        );

        roleRepository.saveAll(roles);
        log.info("初始化角色数据成功，共创建 {} 个角色", roles.size());
    }

    private void initializeAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            log.info("管理员用户已存在，跳过初始化");
            return;
        }

        RoleEntity superAdminRole = roleRepository.findByCode("ROLE_SUPER_ADMIN").orElse(null);
        Set<RoleEntity> roles = new HashSet<>();
        if (superAdminRole != null) {
            roles.add(superAdminRole);
        }

        UserEntity admin = UserEntity.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .status(1)
                .roles(roles)
                .build();

        userRepository.save(admin);
        log.info("初始化管理员用户成功，用户名: admin，密码: admin123");
    }

    private void assignPermissionsToRoles() {
        // 超级管理员拥有所有权限
        RoleEntity superAdmin = roleRepository.findByCode("ROLE_SUPER_ADMIN").orElse(null);
        if (superAdmin != null) {
            Set<PermissionEntity> allPermissions = new HashSet<>(permissionRepository.findAll());
            superAdmin.setPermissions(allPermissions);
            roleRepository.save(superAdmin);
            log.info("为超级管理员分配所有权限成功");
        }

        // 管理员拥有大部分权限（排除用户删除等敏感权限）
        RoleEntity admin = roleRepository.findByCode("ROLE_ADMIN").orElse(null);
        if (admin != null) {
            Set<PermissionEntity> adminPermissions = new HashSet<>(permissionRepository.findAll());
            // 可以根据需要移除一些敏感权限
            admin.setPermissions(adminPermissions);
            roleRepository.save(admin);
            log.info("为管理员分配权限成功");
        }

        // 医生角色拥有医疗相关权限
        RoleEntity doctor = roleRepository.findByCode("ROLE_DOCTOR").orElse(null);
        if (doctor != null) {
            Set<String> doctorPermissionCodes = Set.of(
                    "patient:read", "patient:create", "patient:update",
                    "doctor:read",
                    "appointment:read", "appointment:create", "appointment:update",
                    "medical:read", "medical:create", "medical:update",
                    "medicine:read"
            );
            Set<PermissionEntity> doctorPermissions = new HashSet<>(
                    permissionRepository.findAll().stream()
                            .filter(p -> doctorPermissionCodes.contains(p.getCode()))
                            .toList()
            );
            doctor.setPermissions(doctorPermissions);
            roleRepository.save(doctor);
            log.info("为医生角色分配权限成功");
        }

        // 普通用户仅拥有查看权限
        RoleEntity user = roleRepository.findByCode("ROLE_USER").orElse(null);
        if (user != null) {
            Set<String> userPermissionCodes = Set.of(
                    "patient:read", "doctor:read", "appointment:read", "medicine:read"
            );
            Set<PermissionEntity> userPermissions = new HashSet<>(
                    permissionRepository.findAll().stream()
                            .filter(p -> userPermissionCodes.contains(p.getCode()))
                            .toList()
            );
            user.setPermissions(userPermissions);
            roleRepository.save(user);
            log.info("为普通用户角色分配权限成功");
        }
    }

    /**
     * 初始化菜单数据 — 完整的多级菜单体系
     */
    private void initializeMenus() {
        if (menuRepository.count() > 0) {
            log.info("菜单数据已存在，跳过初始化");
            return;
        }

        // ========== 一级分组菜单 ==========
        MenuEntity patientGroup   = createMenu("患者管理",  null, "🏥",  1, "患者管理", null);
        MenuEntity doctorGroup    = createMenu("医生管理",  null, "👨‍⚕️", 2, "医生管理", null);
        MenuEntity appointGroup   = createMenu("预约管理",  null, "📅",  3, "预约管理", null);
        MenuEntity medicalGroup   = createMenu("病历管理",  null, "📝",  4, "病历管理", null);
        MenuEntity medicineGroup  = createMenu("药品管理",  null, "💊",  5, "药品管理", null);
        MenuEntity albumGroup     = createMenu("相册管理",  null, "📸",  6, "相册管理", null);
        MenuEntity aiGroup        = createMenu("AI 助手",   null, "🤖",  7, "AI助手",   null);
        MenuEntity systemGroup    = createMenu("系统管理",  null, "⚙️",  8, "系统管理", null);

        menuRepository.saveAll(List.of(
                patientGroup, doctorGroup, appointGroup, medicalGroup,
                medicineGroup, albumGroup, aiGroup, systemGroup
        ));

        // ========== 二级子菜单 ==========
        // 患者管理
        MenuEntity patientList  = createMenu("患者列表", "patient-management",      "👥", 1, "患者管理", patientGroup);
        MenuEntity patientAdd   = createMenu("添加患者", "patient-management",      "➕", 2, "患者管理", patientGroup);

        // 医生管理
        MenuEntity doctorList   = createMenu("医生排班", "doctor-schedule",         "📋", 1, "医生管理", doctorGroup);

        // 预约管理
        MenuEntity apptList     = createMenu("预约列表", "appointment-management",  "📋", 1, "预约管理", appointGroup);
        MenuEntity apptAdd      = createMenu("新建预约", "appointment-management",  "➕", 2, "预约管理", appointGroup);

        // 病历管理
        MenuEntity mrList       = createMenu("病历列表", "medical-record",          "📋", 1, "病历管理", medicalGroup);
        MenuEntity mrAdd        = createMenu("新建病历", "medical-record",          "➕", 2, "病历管理", medicalGroup);

        // 药品管理
        MenuEntity medList      = createMenu("药品列表", "medicine-management",     "📋", 1, "药品管理", medicineGroup);

        // 相册管理
        MenuEntity albumPage    = createMenu("我的相册", "photo",                   "🖼️", 1, "相册管理", albumGroup);

        // 系统管理
        MenuEntity menuMgmt     = createMenu("菜单管理", "menu-management",         "📂", 1, "菜单管理", systemGroup);

        // AI 助手
        MenuEntity aiChat      = createMenu("AI 对话",      "ai-chat",    "💬", 1, "AI助手", aiGroup);
        MenuEntity aiMindMap   = createMenu("思维导图",      "quicklyGenerate", "🧠", 2, "AI助手", aiGroup);
        MenuEntity aiTriage    = createMenu("智能导诊",      "ai-triage",  "🔍", 3, "AI助手", aiGroup);

        menuRepository.saveAll(List.of(
                patientList, patientAdd,
                doctorList,
                apptList, apptAdd,
                mrList, mrAdd,
                medList,
                albumPage,
                aiChat, aiMindMap, aiTriage,
                menuMgmt
        ));

        // ========== 关联权限 ==========
        associateMenuWithPermissions(patientGroup, "患者管理");
        associateMenuWithPermissions(patientList, "患者管理");
        associateMenuWithPermissions(patientAdd, "患者管理");

        associateMenuWithPermissions(doctorGroup, "医生管理");
        associateMenuWithPermissions(doctorList, "医生管理");

        associateMenuWithPermissions(appointGroup, "预约管理");
        associateMenuWithPermissions(apptList, "预约管理");
        associateMenuWithPermissions(apptAdd, "预约管理");

        associateMenuWithPermissions(medicalGroup, "病历管理");
        associateMenuWithPermissions(mrList, "病历管理");
        associateMenuWithPermissions(mrAdd, "病历管理");

        associateMenuWithPermissions(medicineGroup, "药品管理");
        associateMenuWithPermissions(medList, "药品管理");

        associateMenuWithPermissions(albumGroup, "相册管理");

        associateMenuWithPermissions(aiGroup, "AI助手");
        associateMenuWithPermissions(aiChat, "AI助手");
        associateMenuWithPermissions(aiMindMap, "AI助手");
        associateMenuWithPermissions(aiTriage, "AI助手");

        associateMenuWithPermissions(systemGroup, "系统管理");
        associateMenuWithPermissions(menuMgmt, "菜单管理");

        log.info("初始化菜单数据成功");
    }

    /**
     * 将菜单关联到指定模块的所有权限
     */
    private void associateMenuWithPermissions(MenuEntity menu, String module) {
        List<PermissionEntity> permissions = permissionRepository.findByModule(module);
        for (PermissionEntity permission : permissions) {
            menu.getPermissions().add(permission);
            permission.getMenus().add(menu);
        }
        menuRepository.save(menu);
    }

    private PermissionEntity createPermission(String name, String code, String module, String description) {
        return PermissionEntity.builder()
                .name(name)
                .code(code)
                .module(module)
                .description(description)
                .build();
    }

    private RoleEntity createRole(String name, String code, String description, boolean isDefault) {
        return RoleEntity.builder()
                .name(name)
                .code(code)
                .description(description)
                .isDefault(isDefault)
                .build();
    }

    private MenuEntity createMenu(String title, String route, String icon,
                                   Integer sortOrder, String module, MenuEntity parent) {
        return MenuEntity.builder()
                .title(title)
                .route(route)
                .icon(icon)
                .sortOrder(sortOrder)
                .isActive(true)
                .module(module)
                .parent(parent)
                .build();
    }
}
