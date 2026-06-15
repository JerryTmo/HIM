package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserIdResponse {
        private String id;
    }
}
