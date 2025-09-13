package com.study.studypal.notification.service.api;

import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingsRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingsResponseDto;

import java.util.UUID;

public interface TeamNotificationSettingsService {
    TeamNotificationSettingsResponseDto getTeamNotificationSettings(UUID userId, UUID teamId);
    TeamNotificationSettingsResponseDto updateTeamNotificationSettings(UUID id, UpdateTeamNotificationSettingsRequestDto request);
}
