package com.study.studypal.notification.service.internal;

import com.study.studypal.notification.dto.internal.NotificationTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationInternalService {
  void createNotification(List<UUID> userIds, NotificationTemplate template);

  void deleteNotificationBefore(LocalDateTime time);

  void evictNotificationCaches(List<UUID> userIds);
}
