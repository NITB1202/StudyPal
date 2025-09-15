package com.study.studypal.notification.service.internal;

import com.study.studypal.notification.dto.internal.CreateNotificationDto;
import java.time.LocalDateTime;

public interface NotificationInternalService {
  void createNotification(CreateNotificationDto request);

  void deleteNotificationBefore(LocalDateTime time);
}
