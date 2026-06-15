package com.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class AlbumRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "插入相冊分組")
    public static class InnerAlbumRequest {

        @NotBlank(message = "相冊名不能為空")
        @Size(max = 100, message = "相冊名長度不能超過100個字符")
        @Schema(description = "相冊名", requiredMode = RequiredMode.REQUIRED, example = "我的相冊")
        private String name;

        @Size(max = 500, message = "描述長度不能超過500個字符")
        @Schema(description = "描述", example = "這是我的相冊描述")
        private String description; // 描述可以為空，去掉 @NotBlank

        @Schema(description = "封面圖片URL", example = "http://example.com/cover.jpg")
        private String coverImageUrl; // 封面可以為空，去掉 @NotBlank

        @NotNull(message = "隱私類型不能為空")
        @Schema(description = "隱私類型：0-公開，1-僅好友，2-僅自己", requiredMode = RequiredMode.REQUIRED)
        private Integer privacyType; // 改用 @NotNull
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Schema(description = "更新相冊信息")
    public static class UpdateAlbumRequest {

        @NotBlank(message = "相冊名不能為空")
        @Schema(description = "相冊名", requiredMode = RequiredMode.REQUIRED)
        private String id;

        @NotBlank(message = "相冊名不能為空")
        @Size(max = 100, message = "相冊名長度不能超過100個字符")
        @Schema(description = "相冊名", requiredMode = RequiredMode.REQUIRED, example = "我的相冊")
        private String name;

        @Size(max = 500, message = "描述長度不能超過500個字符")
        @Schema(description = "描述", example = "這是我的相冊描述")
        private String description; // 描述可以為空，去掉 @NotBlank

        @Schema(description = "封面圖片URL", example = "http://example.com/cover.jpg")
        private String coverImageUrl; // 封面可以為空，去掉 @NotBlank

        @NotNull(message = "隱私類型不能為空")
        @Schema(description = "隱私類型：0-公開，1-僅好友，2-僅自己", requiredMode = RequiredMode.REQUIRED)
        private Integer privacyType; // 改用 @NotNull
    }
}
