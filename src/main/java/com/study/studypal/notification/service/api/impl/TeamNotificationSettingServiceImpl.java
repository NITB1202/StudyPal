package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingResponseDto;
import com.study.studypal.notification.entity.TeamNotificationSetting;
import com.study.studypal.notification.exception.TeamNotificationSettingErrorCode;
import com.study.studypal.notification.repository.TeamNotificationSettingRepository;
import com.study.studypal.notification.service.api.TeamNotificationSettingsService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamNotificationSettingServiceImpl implements TeamNotificationSettingsService {
  private final TeamNotificationSettingRepository teamNotificationSettingsRepository;
  private final ModelMapper modelMapper;

  @Override
  public TeamNotificationSettingResponseDto getTeamNotificationSetting(UUID userId, UUID teamId) {
    TeamNotificationSetting setting =
        teamNotificationSettingsRepository
            .findByUserIdAndTeamId(userId, teamId)
            .orElseThrow(
                () -> new BaseException(TeamNotificationSettingErrorCode.SETTING_NOT_FOUND));

    return modelMapper.map(setting, TeamNotificationSettingResponseDto.class);
  }

  @Override
  public TeamNotificationSettingResponseDto updateTeamNotificationSetting(
      UUID userId, UUID settingId, UpdateTeamNotificationSettingRequestDto request) {
    TeamNotificationSetting setting =
        teamNotificationSettingsRepository
            .findById(settingId)
            .orElseThrow(
                () -> new BaseException(TeamNotificationSettingErrorCode.SETTING_NOT_FOUND));

    if (!userId.equals(teamNotificationSettingsRepository.getUserIdById(settingId))) {
      throw new BaseException(TeamNotificationSettingErrorCode.PERMISSION_UPDATE_SETTING_DENIED);
    }

    modelMapper.map(request, setting);
    teamNotificationSettingsRepository.save(setting);

    return modelMapper.map(setting, TeamNotificationSettingResponseDto.class);
  }
}
