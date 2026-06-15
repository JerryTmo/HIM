package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dto.response.AlbumResponse.GetInitResponse;
import com.example.dto.response.AlbumResponse.InnerAlbumResponse;
import com.example.entity.AlbumEntity;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, String> {
    /**
     * 檢查當前用戶名下相冊是否存在
     * 
     * @param name   用戶名
     * @param userId 相冊
     * @return boolean結果
     */
    boolean existsByNameAndUserId(String name, String userId);

    /**
     * 通過ID查詢相冊信息name,description,coverImageUrl,privacyType
     * 
     * @param id 查詢條件ID
     * @return name,description,coverImageUrl,privacyType信息
     */
    @Query("select name,description,coverImageUrl,privacyType from AlbumEntity a where a.id=:id ")
    InnerAlbumResponse findInnerAlbumById(@Param("id") String id);

    @Query("select new com.example.dto.response.AlbumResponse$GetInitResponse(a.id as id, a.name as name, a.description as description, "
            +
            "a.coverImageUrl as coverImageUrl, a.privacyType as privacyType, a.photoCount as photoCount )" +
            "from AlbumEntity a where a.userId = :userId")
    List<GetInitResponse> findGetInit(@Param("userId") String userId);
}
