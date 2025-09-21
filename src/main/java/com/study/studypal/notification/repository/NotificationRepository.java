package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  void deleteByCreatedAtBefore(LocalDateTime time);

  @Query(
      """
    SELECT n
    FROM Notification n
    WHERE n.user.id = :userId
    ORDER BY n.createdAt DESC
    """)
  List<Notification> findByUserId(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT n
    FROM Notification n
    WHERE n.user.id = :userId
    AND n.createdAt < :cursor
    ORDER BY n.createdAt DESC
    """)
  List<Notification> findByUserIdWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  long countByUserId(UUID userId);

  long countByUserIdAndIsReadFalse(UUID userId);

  @Modifying
  @Query(
      """
    UPDATE Notification n
    SET n.isRead = true
    WHERE n.user.id = :userId
    AND n.id IN :ids
    AND n.isRead = false
    """)
  int markAsReadByIds(@Param("userId") UUID userId, @Param("ids") List<UUID> ids);

  @Modifying
  @Query(
      """
    UPDATE Notification n
    SET n.isRead = true
    WHERE n.user.id = :userId
    AND n.isRead = false
    """)
  void markAllAsRead(@Param("userId") UUID userId);

  @Modifying
  @Query(
      """
    DELETE FROM Notification n
    WHERE n.user.id = :userId
    AND n.id IN :ids
    """)
  int deleteByIds(@Param("userId") UUID userId, @Param("ids") List<UUID> ids);

  void deleteAllByUserId(UUID userId);
}
