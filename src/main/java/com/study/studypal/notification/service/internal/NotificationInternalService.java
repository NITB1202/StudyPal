package com.study.studypal.notification.service.internal;

import com.study.studypal.notification.dto.internal.CreateNotificationRequest;
import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationInternalService {
  void createNotification(CreateNotificationRequest request);

  void deleteNotificationBefore(LocalDateTime time);

  void evictNotificationCache(UUID userId);
}
