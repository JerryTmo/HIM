package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 相册类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbum {
    private String name;
    private String icon;
    private int photoCount;

    // public PhotoAlbum(String name, String icon, int photoCount) {
    // this.name = name;
    // this.icon = icon;
    // this.photoCount = photoCount;
    // }

    // public String getName() {
    // return name;
    // }

    // public String getIcon() {
    // return icon;
    // }

    // public int getPhotoCount() {
    // return photoCount;
    // }
}