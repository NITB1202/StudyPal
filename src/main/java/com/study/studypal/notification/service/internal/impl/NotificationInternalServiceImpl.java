package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import com.study.studypal.notification.entity.Notification;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationInternalServiceImpl implements NotificationInternalService {
  private final NotificationRepository notificationRepository;
  private final CacheManager cacheManager;
  private final ModelMapper modelMapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createNotification(List<UUID> userIds, NotificationTemplate template) {
    LocalDateTime now = LocalDateTime.now();
    List<Notification> notifications = new ArrayList<>();

    for (UUID userId : userIds) {
      Notification notification = modelMapper.map(template, Notification.class);
      User user = entityManager.getReference(User.class, userId);

      notification.setCreatedAt(now);
      notification.setUser(user);
      notification.setIsRead(false);

      notifications.add(notification);
    }

    notificationRepository.saveAll(notifications);
  }

  @Override
  @Transactional
  public void deleteNotificationBefore(LocalDateTime time) {
    notificationRepository.deleteByCreatedAtBefore(time);
  }

  @Override
  public void evictNotificationCaches(List<UUID> userIds) {
    Cache cache = cacheManager.getCache(CacheNames.NOTIFICATIONS);
    if (cache == null) {
      return;
    }

    for (UUID userId : userIds) {
      cache.evictIfPresent(CacheKeyUtils.of(userId));
    }
  }
}
