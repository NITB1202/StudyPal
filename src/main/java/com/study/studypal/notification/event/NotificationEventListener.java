package com.study.studypal.notification.event;

import com.study.studypal.notification.dto.internal.CreateNotificationRequest;
import com.study.studypal.notification.enums.LinkedSubject;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.team.event.invitation.InvitationCreatedEvent;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.event.team.UserJoinedTeamEvent;
import com.study.studypal.team.event.team.UserLeftTeamEvent;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.service.internal.UserInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
  private final DeviceTokenInternalService deviceTokenService;
  private final UserInternalService userService;
  private final TeamInternalService teamService;
  private final NotificationInternalService notificationService;
  private final TeamNotificationSettingInternalService settingService;

  @Async
  @EventListener
  public void handleInvitationCreatedEvent(InvitationCreatedEvent event) {
    UserSummaryProfile inviter = userService.getUserSummaryProfile(event.getInviterId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "Team invitation";
    String content = inviter.getName() + " has invited you to the team " + teamName + ".";

    CreateNotificationRequest notification =
        CreateNotificationRequest.builder()
            .userId(event.getInviteeId())
            .title(title)
            .content(content)
            .subject(LinkedSubject.INVITATION)
            .subjectId(event.getInvitationId())
            .build();

    deviceTokenService.sendPushNotification(notification);
  }

  @Async
  @EventListener
  public void handleTeamDeletedEvent(TeamDeletedEvent event) {
    UserSummaryProfile deletedBy = userService.getUserSummaryProfile(event.getDeletedBy());

    String title = "Team deleted";
    String content =
        "Team " + event.getTeamName() + " has been deleted by " + deletedBy.getName() + ".";

    for (UUID memberId : event.getMemberIds()) {
      if (event.getDeletedBy().equals(memberId)) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(null)
              .build();

      notificationService.createNotification(dto);
      notificationService.evictNotificationCache(memberId);
      deviceTokenService.sendPushNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleTeamUpdatedEvent(TeamUpdatedEvent event) {
    UserSummaryProfile updatedBy = userService.getUserSummaryProfile(event.getUpdatedBy());

    String title = "Team updated";
    String content =
        updatedBy.getName()
            + " has updated team "
            + event.getTeamName()
            + "'s general information.";

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUpdatedBy().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      notificationService.createNotification(dto);
      notificationService.evictNotificationCache(memberId);
      deviceTokenService.sendPushNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleUserJoinedTeamEvent(UserJoinedTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "New team member";
    String content = user.getName() + " has joined the " + teamName + " team.";

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUserId().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      notificationService.createNotification(dto);
      notificationService.evictNotificationCache(memberId);
      deviceTokenService.sendPushNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleUserLeftTeamEvent(UserLeftTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "Member left";
    String content = user.getName() + " has left team " + teamName + ".";

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUserId().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      notificationService.createNotification(dto);
      notificationService.evictNotificationCache(memberId);
      deviceTokenService.sendPushNotification(dto);
    }
  }
}
