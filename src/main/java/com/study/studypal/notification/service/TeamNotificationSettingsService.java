package com.study.studypal.notification.service;

import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingsRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingsResponseDto;

import java.util.UUID;

public interface TeamNotificationSettingsService {
    void createSettings(UUID userId, UUID teamId);
    TeamNotificationSettingsResponseDto getTeamNotificationSettings(UUID userId, UUID teamId);
    TeamNotificationSettingsResponseDto updateTeamNotificationSettings(UUID id, UpdateTeamNotificationSettingsRequestDto request);
}
