package com.study.studypal.notification.event;

import com.study.studypal.notification.dto.internal.CreateNotificationRequest;
import com.study.studypal.notification.enums.LinkedSubject;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.plan.event.plan.PlanCompletedEvent;
import com.study.studypal.plan.event.task.TaskAssignedEvent;
import com.study.studypal.plan.event.task.TaskRemindedEvent;
import com.study.studypal.plan.event.task.TaskUpdatedEvent;
import com.study.studypal.team.event.invitation.InvitationCreatedEvent;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.event.team.UserJoinedTeamEvent;
import com.study.studypal.team.event.team.UserLeftTeamEvent;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.service.internal.UserInternalService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    String content = String.format("%s invited you to join %s.", inviter.getName(), teamName);

    CreateNotificationRequest notification =
        CreateNotificationRequest.builder()
            .userId(event.getInviteeId())
            .imageUrl(inviter.getAvatarUrl())
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
    String content = String.format("%s deleted %s.", deletedBy.getName(), event.getTeamName());

    for (UUID memberId : event.getMemberIds()) {
      if (event.getDeletedBy().equals(memberId)) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .imageUrl(deletedBy.getAvatarUrl())
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(null)
              .build();

      processNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleTeamUpdatedEvent(TeamUpdatedEvent event) {
    UserSummaryProfile updatedBy = userService.getUserSummaryProfile(event.getUpdatedBy());

    String title = "Team updated";
    String content =
        String.format(
            "%s updated the general information of %s", updatedBy.getName(), event.getTeamName());

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUpdatedBy().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .imageUrl(updatedBy.getAvatarUrl())
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      processNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleUserJoinedTeamEvent(UserJoinedTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "New team member";
    String content = String.format("%s joined %s.", user.getName(), teamName);

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUserId().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .imageUrl(user.getAvatarUrl())
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      processNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleUserLeftTeamEvent(UserLeftTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "Member left";
    String content = String.format("%s left %s.", user.getName(), teamName);

    for (UUID memberId : event.getMemberIds()) {
      if (event.getUserId().equals(memberId)
          || !settingService.getTeamNotificationSetting(memberId, event.getTeamId())) continue;

      CreateNotificationRequest dto =
          CreateNotificationRequest.builder()
              .userId(memberId)
              .imageUrl(user.getAvatarUrl())
              .title(title)
              .content(content)
              .subject(LinkedSubject.TEAM)
              .subjectId(event.getTeamId())
              .build();

      processNotification(dto);
    }
  }

  @Async
  @EventListener
  public void handleTaskAssignedEvent(TaskAssignedEvent event) {
    if (event.getAssignerId().equals(event.getAssigneeId())) return;

    UserSummaryProfile assigner = userService.getUserSummaryProfile(event.getAssignerId());

    String title = "New task assigned";
    String content =
        String.format("%s assigned task [%s] to you.", assigner.getName(), event.getTaskCode());

    CreateNotificationRequest dto =
        CreateNotificationRequest.builder()
            .userId(event.getAssigneeId())
            .imageUrl(assigner.getAvatarUrl())
            .title(title)
            .content(content)
            .subject(LinkedSubject.TASK)
            .subjectId(event.getTaskId())
            .build();

    processNotification(dto);
  }

  @Async
  @EventListener
  public void handleTaskRemindedEvent(TaskRemindedEvent event) {
    UUID userId = event.getUserId();
    UUID teamId = event.getTeamId();

    if (teamId != null && !settingService.getTeamPlanReminderSetting(userId, teamId)) return;

    boolean isOverDueTask = event.getDueDate().isBefore(LocalDateTime.now());
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    String imageUrl = teamId != null ? teamService.getTeamAvatarUrl(teamId) : null;
    String title = isOverDueTask ? "Expired task" : "Task reminded";
    String content =
        isOverDueTask
            ? String.format("Task [%s] is overdue.", event.getTaskCode())
            : String.format(
                "Task [%s] will expire at %s on %s.",
                event.getTaskCode(),
                event.getDueDate().format(timeFormatter),
                event.getDueDate().format(dateFormatter));

    CreateNotificationRequest dto =
        CreateNotificationRequest.builder()
            .userId(userId)
            .imageUrl(imageUrl)
            .title(title)
            .content(content)
            .subject(LinkedSubject.TASK)
            .subjectId(event.getTaskId())
            .build();

    processNotification(dto);
  }

  @Async
  @EventListener
  public void handleTaskUpdatedEvent(TaskUpdatedEvent event) {
    if (event.getUserId().equals(event.getAssigneeId())) return;

    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = "Task updated";
    String content = String.format("%s updated task [%s].", user.getName(), event.getTaskCode());

    CreateNotificationRequest dto =
        CreateNotificationRequest.builder()
            .userId(event.getAssigneeId())
            .imageUrl(user.getAvatarUrl())
            .title(title)
            .content(content)
            .subject(LinkedSubject.TASK)
            .subjectId(event.getTaskId())
            .build();

    processNotification(dto);
  }

  @Async
  @EventListener
  public void handlePlanCompletedEvent(PlanCompletedEvent event) {
    String title = "Plan completed";
    String content = String.format("Plan [%s] is completed.", event.getPlanCode());

    CreateNotificationRequest dto =
        CreateNotificationRequest.builder()
            .userId(event.getCreatorId())
            .imageUrl(event.getTeamAvatarUrl())
            .title(title)
            .content(content)
            .subject(LinkedSubject.PLAN)
            .subjectId(event.getPlanId())
            .build();

    processNotification(dto);
  }

  private void processNotification(CreateNotificationRequest request) {
    notificationService.createNotification(request);
    notificationService.evictNotificationCache(request.getUserId());
    deviceTokenService.sendPushNotification(request);
  }
}
