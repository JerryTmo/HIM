package com.example.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "上傳相冊")
public class PhoneRequest {
    private List<MultipartFile> files;

}
