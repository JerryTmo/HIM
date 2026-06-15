package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.ServiceResult;
import com.example.dto.request.PhoneRequest;
import com.example.dto.response.PhoneResponse.InnerPhoneResponse;
import com.example.service.PhoneService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "相冊模塊")
@RestController
@RequestMapping("/phone")
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;

    @PostMapping(value = "/insertPhone", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "插入相冊")
    public ServiceResult<Integer> insertPhone(@RequestPart("files") List<MultipartFile> files) {
        return this.phoneService.insertPhone(files);
    }

    @GetMapping("/getPhoneInit")
    @Operation(summary = "獲取當前用戶相冊詳情")
    public ServiceResult<List<InnerPhoneResponse>> getInit() {
        return this.phoneService.getInit();
    }
}
