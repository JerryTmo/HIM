package com.example.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "album", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_album", columnNames = { "user_id", "name"
        })
}, indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_privacy_type", columnList = "privacy_type"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 100, columnDefinition = "varchar(100) comment '用戶ID，關聯到用戶表'")
    private String userId;

    @Column(name = "name", nullable = false, length = 100, columnDefinition = "varchar(100) comment '相冊名稱，同一用戶下唯一'")
    private String name;

    @Column(name = "description", length = 500, columnDefinition = "varchar(500) comment '相冊描述'")
    private String description;

    @Column(name = "cover_image_url", length = 500, columnDefinition = "varchar(500) comment '封面圖片URL'")
    private String coverImageUrl;

    @Column(name = "privacy_type", nullable = false, columnDefinition = "tinyint default 0 comment '隱私類型：0-公開，1-僅好友，2-僅自己'")
    private Integer privacyType;

    @Column(name = "photo_count", nullable = false, columnDefinition = "int unsigned default 0 comment '相冊中的照片數量'")
    private Integer photoCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "datetime comment '創建時間'")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "datetime comment '更新時間'")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhotoEntity> photos;
}