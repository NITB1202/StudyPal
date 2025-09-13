package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.notification.entity.TeamNotificationSettings;
import com.study.studypal.notification.repository.TeamNotificationSettingsRepository;
import com.study.studypal.notification.service.internal.TeamNotificationSettingsInternalService;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamNotificationSettingsInternalServiceImpl implements TeamNotificationSettingsInternalService {
    private final TeamNotificationSettingsRepository teamNotificationSettingsRepository;
    private final TeamMembershipInternalService teamMembershipInternalService;

    @Override
    public void createSettings(UUID userId, UUID teamId) {
        TeamUser membership = teamMembershipInternalService.getMemberShip(teamId, userId);

        TeamNotificationSettings settings = TeamNotificationSettings.builder()
                .membership(membership)
                .teamNotification(true)
                .chatNotification(true)
                .teamPlanReminder(true)
                .build();

        teamNotificationSettingsRepository.save(settings);
    }
}
