package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.notification.entity.TeamNotificationSetting;
import com.study.studypal.notification.repository.TeamNotificationSettingRepository;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamNotificationSettingInternalServiceImpl
    implements TeamNotificationSettingInternalService {
  private final TeamNotificationSettingRepository teamNotificationSettingsRepository;
  private final TeamMembershipInternalService teamMembershipInternalService;

  @Override
  public void createSettings(UUID userId, UUID teamId) {
    TeamUser membership = teamMembershipInternalService.getMemberShip(teamId, userId);

    TeamNotificationSetting setting =
        TeamNotificationSetting.builder()
            .membership(membership)
            .teamNotification(true)
            .chatNotification(true)
            .teamPlanReminder(true)
            .build();

    teamNotificationSettingsRepository.save(setting);
  }
}
