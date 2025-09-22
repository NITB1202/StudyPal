package com.study.studypal.notification.service.internal;

import com.study.studypal.notification.dto.internal.CreateNotificationDto;
import java.time.LocalDateTime;

public interface DeviceTokenInternalService {
  void deleteDeviceTokenBefore(LocalDateTime time);

  void sendPushNotification(CreateNotificationDto dto);
}
