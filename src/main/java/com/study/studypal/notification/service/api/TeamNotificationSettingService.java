package com.study.studypal.notification.service.api;

import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingResponseDto;
import java.util.UUID;

public interface TeamNotificationSettingService {
  TeamNotificationSettingResponseDto getTeamNotificationSetting(UUID userId, UUID teamId);

  TeamNotificationSettingResponseDto updateTeamNotificationSetting(
      UUID userId, UUID settingId, UpdateTeamNotificationSettingRequestDto request);
}
