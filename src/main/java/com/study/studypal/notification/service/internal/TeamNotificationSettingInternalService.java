package com.study.studypal.notification.service.internal;

import java.util.UUID;

public interface TeamNotificationSettingInternalService {
  void createSettings(UUID userId, UUID teamId);

  boolean getTeamNotificationSetting(UUID userId, UUID teamId);

  boolean getTeamPlanReminderSetting(UUID userId, UUID teamId);

  boolean getChatNotificationSetting(UUID userId, UUID teamId);
}
