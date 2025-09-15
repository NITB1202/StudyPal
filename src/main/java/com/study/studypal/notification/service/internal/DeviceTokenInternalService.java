package com.study.studypal.notification.service.internal;

import java.time.LocalDateTime;

public interface DeviceTokenInternalService {
  void deleteDeviceTokenBefore(LocalDateTime time);
}
