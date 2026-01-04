package com.study.studypal.notification.service.internal;

import com.study.studypal.notification.dto.internal.NotificationTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeviceTokenInternalService {
  void deleteDeviceTokenBefore(LocalDateTime time);

  void sendPushNotification(List<UUID> recipients, NotificationTemplate template);
}
