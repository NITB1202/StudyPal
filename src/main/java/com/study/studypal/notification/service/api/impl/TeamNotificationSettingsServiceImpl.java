package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingsRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingsResponseDto;
import com.study.studypal.notification.entity.TeamNotificationSettings;
import com.study.studypal.notification.exception.TeamNotificationSettingsErrorCode;
import com.study.studypal.notification.repository.TeamNotificationSettingsRepository;
import com.study.studypal.notification.service.api.TeamNotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamNotificationSettingsServiceImpl implements TeamNotificationSettingsService {
    private final TeamNotificationSettingsRepository teamNotificationSettingsRepository;
    private final ModelMapper modelMapper;

    @Override
    public TeamNotificationSettingsResponseDto getTeamNotificationSettings(UUID userId, UUID teamId) {
        TeamNotificationSettings settings = teamNotificationSettingsRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new BaseException(TeamNotificationSettingsErrorCode.SETTINGS_NOT_FOUND)
        );

        return modelMapper.map(settings, TeamNotificationSettingsResponseDto.class);
    }

    @Override
    public TeamNotificationSettingsResponseDto updateTeamNotificationSettings(UUID id, UpdateTeamNotificationSettingsRequestDto request) {
        TeamNotificationSettings settings = teamNotificationSettingsRepository.findById(id).orElseThrow(
                () -> new BaseException(TeamNotificationSettingsErrorCode.SETTINGS_NOT_FOUND)
        );

        modelMapper.map(request, settings);
        teamNotificationSettingsRepository.save(settings);

        return modelMapper.map(settings, TeamNotificationSettingsResponseDto.class);
    }
}
