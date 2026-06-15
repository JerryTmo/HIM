package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ServiceResult;
import com.example.dto.request.AlbumRequest.InnerAlbumRequest;
import com.example.dto.request.AlbumRequest.UpdateAlbumRequest;
import com.example.dto.response.AlbumResponse.GetInitResponse;
import com.example.service.AlbumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "相冊分組")
@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/insert")
    @Operation(summary = "插入相冊")
    public ServiceResult<Integer> insertAlbum(@Valid @RequestBody InnerAlbumRequest innerAlbumRequest) {
        return this.albumService.insertAlbum(innerAlbumRequest);
    }

    @PostMapping("/update")
    @Operation(summary = "更新相冊")
    public void updateAlbum(@Valid @RequestBody UpdateAlbumRequest updateAlbumRequest) {
        this.albumService.updateAlbum(updateAlbumRequest);
    }

    @GetMapping("/getInit")
    @Operation(summary = "初始化相冊")
    public ServiceResult<List<GetInitResponse>> getInit() {
        return this.albumService.getInit();
    }
}
