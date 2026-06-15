package com.example.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photos", indexes = {
        @Index(name = "idx_album_id", columnList = "album_id, sort_order"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "album_id", nullable = false, length = 255, columnDefinition = "varchar(255)")
    private String albumId;

    @Column(name = "user_id", nullable = false, length = 255, columnDefinition = "varchar(255)")
    private String userId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "width", columnDefinition = "smallint unsigned")
    private Integer width;

    @Column(name = "height", columnDefinition = "smallint unsigned")
    private Integer height;

    @Column(name = "size", columnDefinition = "int unsigned")
    private Long size; // 字节数使用Long

    @Column(name = "sort_order", nullable = false, columnDefinition = "int default 0")
    private Integer sortOrder;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint default 1")
    private Byte status; // 0-删除/回收站, 1-正常

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 关联关系：多张照片属于一个相册
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", insertable = false, updatable = false)
    private AlbumEntity album;
}
