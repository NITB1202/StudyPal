package com.study.studypal.chat.repository;

import com.study.studypal.chat.entity.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
  @Query(
      """
    SELECT m
    FROM Message m
    WHERE m.team.id = :teamId
    ORDER BY m.createdAt DESC
    """)
  List<Message> findByTeamId(@Param("teamId") UUID teamId, Pageable pageable);

  @Query(
      """
    SELECT m
    FROM Message m
    WHERE m.team.id = :teamId
    AND m.createdAt < :cursor
    ORDER BY m.createdAt DESC
    """)
  List<Message> findByTeamIdWithCursor(
      @Param("teamId") UUID teamId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  long countByTeamId(UUID teamId);

  @Query("""
    SELECT m
    FROM Message m
    JOIN FETCH m.team
    WHERE m.id = :id
    """)
  Optional<Message> findByIdWithTeam(UUID id);

  @Query(
      """
    SELECT m
    FROM Message m
    WHERE m.team.id = :teamId
    AND m.createdAt < :time
    ORDER BY m.createdAt DESC
    """)
  List<Message> findMessagesBefore(@Param("teamId") UUID teamId, @Param("time") LocalDateTime time);

  List<Message> findAllByCreatedAtBefore(LocalDateTime time);
}
