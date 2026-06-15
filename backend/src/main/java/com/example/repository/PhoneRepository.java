package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dto.response.PhoneResponse.InnerPhoneResponse;
import com.example.entity.PhotoEntity;

@Repository
public interface PhoneRepository extends JpaRepository<PhotoEntity, String> {

    @Query("SELECT new com.example.dto.response.PhoneResponse$InnerPhoneResponse(" +
            "id, title,albumId, url, thumbnailUrl, createdAt) " +
            "FROM PhotoEntity a WHERE a.userId = :userId AND a.status = 1")
    List<InnerPhoneResponse> findByUserId(@Param("userId") String userId);
}
