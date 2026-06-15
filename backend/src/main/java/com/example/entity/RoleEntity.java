package com.example.entity;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    /**
     * 角色名稱（顯示用）
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 角色代碼（用於程式判斷，如 ROLE_ADMIN）
     */
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    /**
     * 角色描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 是否為預設角色
     * 0: 否, 1: 是
     */
    @Builder.Default
    @Column(name = "is_default", nullable = false, columnDefinition = "tinyint default 0")
    private Boolean isDefault = false;

    /**
     * 創建時間
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 如果需要雙向關聯到 User（透過中間表）
     */
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new LinkedHashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new LinkedHashSet<>();

    /**
     * 實體生命週期回調：在持久化前自動設置創建時間
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleEntity that = (RoleEntity) o;
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
        return "RoleEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                ", createdAt=" + createdAt +
                '}';
    }
}
