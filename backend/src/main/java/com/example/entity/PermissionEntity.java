package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;

    @Column(name = "module", length = 50, nullable = false)
    private String module;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 雙向關聯到 Role
    @ManyToMany(mappedBy = "permissions")
    private Set<RoleEntity> roles = new LinkedHashSet<>();

    // 關聯到 Menu（通過中間表 permissions_menus）
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "permissions_menus", joinColumns = @JoinColumn(name = "permission_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private Set<MenuEntity> menus = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PermissionEntity that = (PermissionEntity) o;
        // 只使用業務主鍵或 ID 進行比較
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // 只使用 ID 計算 hashCode，避免加載關聯集合
        return id != null ? Objects.hash(id) : super.hashCode();
    }

    @Override
    public String toString() {
        return "PermissionEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", module='" + module + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}