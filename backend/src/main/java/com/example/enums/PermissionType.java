package com.example.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum PermissionType {
    CREATE("新增", "CREATE"),
    DELETE("刪除", "DELETE"),
    UPDATE("修改", "UPDATE"),
    VIEW("查詢", "VIEW");

    private final String name;
    private final String code;

    PermissionType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static List<PermissionType> getAll() {
        return Arrays.asList(values());
    }

    public String generatePermissionName(String module) {
        return module + this.name;
    }

    public String generatePermissionCode(String module) {
        return module.toUpperCase() + "_" + this.code;
    }

    public String generateDescription(String module) {
        return module + "模塊" + this.name + "權限";
    }
}
