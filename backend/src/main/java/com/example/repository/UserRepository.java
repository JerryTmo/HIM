package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRoles(@Param("username") String username);

    // 根據用戶名查詢
    Optional<UserEntity> findByUsername(String username);

    // 检查用户名是否存在
    Boolean existsByUsername(String username);

    // 查詢ID
    @Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
    String findIdByUsername(@Param("username") String username);
}