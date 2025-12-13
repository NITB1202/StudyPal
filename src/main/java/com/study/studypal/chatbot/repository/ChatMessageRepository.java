package com.study.studypal.chatbot.repository;

import com.study.studypal.chatbot.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
  @Query(
      """
    SELECT c
    FROM ChatMessage c
    WHERE c.user.id = :userId
    ORDER BY c.createdAt DESC
    """)
  List<ChatMessage> findByUserId(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT c
    FROM ChatMessage c
    WHERE c.user.id = :userId
    AND c.createdAt < :cursor
    ORDER BY c.createdAt DESC
    """)
  List<ChatMessage> findByUserIdWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  long countByUserId(UUID userId);
}
