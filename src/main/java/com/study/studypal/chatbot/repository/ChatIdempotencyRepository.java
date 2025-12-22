package com.study.studypal.chatbot.repository;

import com.study.studypal.chatbot.entity.ChatIdempotency;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatIdempotencyRepository extends JpaRepository<ChatIdempotency, UUID> {
  Optional<ChatIdempotency> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
    SELECT c
    FROM ChatIdempotency c
    WHERE c.user.id = :userId
    AND c.idempotencyKey = :idempotencyKey
    """)
  Optional<ChatIdempotency> findByUserIdAndIdempotencyKeyForUpdate(
      @Param("userId") UUID userId, @Param("idempotencyKey") String idempotencyKey);
}
