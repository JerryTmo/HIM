package com.example.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PhoneResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerPhoneResponse {
        private String id;
        private String title;
        private String albumId;
        private String url;
        private String thumbnailUrl;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        private LocalDateTime createdAt;
    }
}
