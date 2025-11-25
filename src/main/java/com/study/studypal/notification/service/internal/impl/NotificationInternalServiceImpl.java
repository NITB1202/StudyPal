package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.notification.dto.internal.CreateNotificationRequest;
import com.study.studypal.notification.entity.Notification;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationInternalServiceImpl implements NotificationInternalService {
  private final NotificationRepository notificationRepository;
  private final CacheManager cacheManager;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createNotification(CreateNotificationRequest request) {
    User user = entityManager.getReference(User.class, request.getUserId());

    Notification notification =
        Notification.builder()
            .user(user)
            .imageUrl(request.getImageUrl())
            .title(request.getTitle())
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .isRead(false)
            .subject(request.getSubject())
            .subjectId(request.getSubjectId())
            .build();

    notificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void deleteNotificationBefore(LocalDateTime time) {
    notificationRepository.deleteByCreatedAtBefore(time);
  }

  @Override
  public void evictNotificationCache(UUID userId) {
    Cache cache = cacheManager.getCache(CacheNames.NOTIFICATIONS);
    Objects.requireNonNull(cache).evictIfPresent(CacheKeyUtils.of(userId));
  }
}
