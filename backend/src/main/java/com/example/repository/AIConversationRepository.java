package com.example.repository;

import com.example.entity.AIConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIConversationRepository extends JpaRepository<AIConversationEntity, String> {

    List<AIConversationEntity> findByUserIdAndSessionIdOrderByCreatedAtAsc(String userId, String sessionId);

    List<AIConversationEntity> findByUserIdOrderByCreatedAtDesc(String userId);

    List<AIConversationEntity> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, String type);
}
