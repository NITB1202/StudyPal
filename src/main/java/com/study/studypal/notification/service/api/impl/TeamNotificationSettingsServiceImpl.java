package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingsRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingsResponseDto;
import com.study.studypal.notification.entity.TeamNotificationSettings;
import com.study.studypal.notification.exception.TeamNotificationSettingsErrorCode;
import com.study.studypal.notification.repository.TeamNotificationSettingsRepository;
import com.study.studypal.notification.service.api.TeamNotificationSettingsService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamNotificationSettingsServiceImpl implements TeamNotificationSettingsService {
  private final TeamNotificationSettingsRepository teamNotificationSettingsRepository;
  private final ModelMapper modelMapper;

  @Override
  public TeamNotificationSettingsResponseDto getTeamNotificationSettings(UUID userId, UUID teamId) {
    TeamNotificationSettings settings =
        teamNotificationSettingsRepository
            .findByUserIdAndTeamId(userId, teamId)
            .orElseThrow(
                () -> new BaseException(TeamNotificationSettingsErrorCode.SETTING_NOT_FOUND));

    return modelMapper.map(settings, TeamNotificationSettingsResponseDto.class);
  }

  @Override
  public TeamNotificationSettingsResponseDto updateTeamNotificationSettings(
      UUID userId, UUID settingId, UpdateTeamNotificationSettingsRequestDto request) {
    TeamNotificationSettings settings =
        teamNotificationSettingsRepository
            .findById(settingId)
            .orElseThrow(
                () -> new BaseException(TeamNotificationSettingsErrorCode.SETTING_NOT_FOUND));

    if (!userId.equals(teamNotificationSettingsRepository.getUserIdById(settingId))) {
      throw new BaseException(TeamNotificationSettingsErrorCode.PERMISSION_UPDATE_SETTING_DENIED);
    }

    modelMapper.map(request, settings);
    teamNotificationSettingsRepository.save(settings);

    return modelMapper.map(settings, TeamNotificationSettingsResponseDto.class);
  }
}
