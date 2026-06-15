package com.example.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.ServiceResult;
import com.example.dto.response.PhoneResponse.InnerPhoneResponse;
import com.example.entity.AlbumEntity;
import com.example.entity.PhotoEntity;
import com.example.enums.ResultCode;
import com.example.exception.BusinessException;
import com.example.repository.AlbumRepository;
import com.example.repository.PhoneRepository;
import com.example.service.PhoneService;
import com.example.util.UserUtils;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url}")
    private String fileAccessUrl;

    private final PhoneRepository photoRepository;
    private final AlbumRepository albumRepository;
    private final UserUtils userUtils;

    @Override
    public ServiceResult<List<InnerPhoneResponse>> getInit() {
        String userId = this.userUtils.getUserId();
        List<InnerPhoneResponse> innerPhoneResponses = this.photoRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(innerPhoneResponses)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(innerPhoneResponses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Integer> insertPhone(List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return ServiceResult.success(0);
        }

        String userId = this.userUtils.getUserId();
        AlbumEntity album = getAlbumOrThrow();

        List<PhotoEntity> photos = new ArrayList<>();
        int success = 0;

        for (MultipartFile file : files) {
            try {
                photos.add(processFile(file, userId));
                success++;
            } catch (Exception e) {
                log.error("文件處理失敗: {}", file.getOriginalFilename(), e);
            }
        }

        if (!photos.isEmpty()) {
            photoRepository.saveAll(photos);
            album.setPhotoCount(album.getPhotoCount() + success);
            album.setUpdatedAt(LocalDateTime.now());
            albumRepository.save(album);
        }

        return ServiceResult.success(success);
    }

    private AlbumEntity getAlbumOrThrow() {
        String albumId = "da55a961-dbf4-4055-8cc5-f60f672d9488";
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("專輯不存在: " + albumId));
    }

    private PhotoEntity processFile(MultipartFile file, String userId) throws IOException {
        validateFile(file);

        // 保存文件並獲取信息
        FileSaveResult result = saveAndGetInfo(file);

        return PhotoEntity.builder()
                .albumId("da55a961-dbf4-4055-8cc5-f60f672d9488")
                .userId(userId)
                .title(getBaseName(file.getOriginalFilename()))
                .url(result.url)
                .thumbnailUrl(result.thumbnailUrl)
                .width(result.width)
                .height(result.height)
                .size(file.getSize())
                .sortOrder(0)
                .status((byte) 1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!Set.of(".jpg", ".jpeg", ".png", ".gif").contains(ext)) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
    }

    private FileSaveResult saveAndGetInfo(MultipartFile file) throws IOException {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());

        Path dir = Paths.get(uploadPath, datePath);
        Files.createDirectories(dir);

        Path filePath = dir.resolve(newName);
        file.transferTo(filePath.toFile());

        ImageInfo imageInfo = readImageInfo(filePath);

        return new FileSaveResult(
                fileAccessUrl + "/" + datePath + "/" + newName,
                fileAccessUrl + "/thumbnail/" + datePath + "/" + newName,
                imageInfo.width,
                imageInfo.height);
    }

    private ImageInfo readImageInfo(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            return new ImageInfo(img.getWidth(), img.getHeight());
        }
    }

    private String getBaseName(String filename) {
        int dot = filename.lastIndexOf(".");
        return dot > 0 ? filename.substring(0, dot) : filename;
    }

    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf(".");
        return dot > 0 ? filename.substring(dot) : "";
    }

    // 簡單的記錄類（可用 record 代替）
    private record FileSaveResult(String url, String thumbnailUrl, int width, int height) {
    }

    private record ImageInfo(int width, int height) {
    }
}
