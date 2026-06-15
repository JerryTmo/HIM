package com.example.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    private String id;
    private String name;
    private LocalDateTime uploadTime;
    private String albumId;
    private String url;
    private long size;

    // public Photo(String id, String name, LocalDateTime uploadTime, int albumId,
    // long size) {
    // this.id = id;
    // this.name = name;
    // this.uploadTime = uploadTime;
    // this.albumId = albumId;
    // this.size = size;
    // }

    // public String getId() {
    // return id;
    // }

    // public String getName() {
    // return name;
    // }

    // public LocalDateTime getUploadTime() {
    // return uploadTime;
    // }

    // public int getAlbumId() {
    // return albumId;
    // }

    // public long getSize() {
    // return size;
    // }
}
