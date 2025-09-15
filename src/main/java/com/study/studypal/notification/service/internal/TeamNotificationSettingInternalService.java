package com.study.studypal.notification.service.internal;

import java.util.UUID;

public interface TeamNotificationSettingInternalService {
  void createSettings(UUID userId, UUID teamId);
}
