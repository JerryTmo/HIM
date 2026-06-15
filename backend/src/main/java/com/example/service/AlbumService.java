package com.example.service;

import java.util.List;

import com.example.common.ServiceResult;
import com.example.dto.request.AlbumRequest.InnerAlbumRequest;
import com.example.dto.request.AlbumRequest.UpdateAlbumRequest;
import com.example.dto.response.AlbumResponse.GetInitResponse;

public interface AlbumService {

    /**
     * 插入相冊
     * 
     * @param innerAlbumRequest 封裝相冊信息
     * @return 插入結果
     */
    ServiceResult<Integer> insertAlbum(InnerAlbumRequest innerAlbumRequest);

    /**
     * 更新相冊
     * 
     * @param innerAlbumRequest 封裝相冊信息
     */
    void updateAlbum(UpdateAlbumRequest updateAlbumRequest);

    /**
     * 初始化相冊
     * 
     * @return 相冊模組
     */
    ServiceResult<List<GetInitResponse>> getInit();
}
