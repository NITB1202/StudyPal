package com.study.studypal.notification.service.internal;

import java.util.UUID;

public interface TeamNotificationSettingsInternalService {
  void createSettings(UUID userId, UUID teamId);
}
