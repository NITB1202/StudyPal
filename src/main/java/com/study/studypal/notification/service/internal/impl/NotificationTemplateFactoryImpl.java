package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import com.study.studypal.notification.enums.LinkedSubject;
import com.study.studypal.notification.service.internal.NotificationTemplateFactory;
import com.study.studypal.plan.event.plan.PlanCompletedEvent;
import com.study.studypal.plan.event.plan.PlanDeletedEvent;
import com.study.studypal.plan.event.plan.PlanUpdatedEvent;
import com.study.studypal.plan.event.task.TaskAssignedEvent;
import com.study.studypal.plan.event.task.TaskDeletedEvent;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateFactoryImpl implements NotificationTemplateFactory {
  private final UserInternalService userService;
  private final TeamInternalService teamService;

  @Override
  public NotificationTemplate getInvitationCreatedTemplate(InvitationCreatedEvent event) {
    UserSummaryProfile inviter = userService.getUserSummaryProfile(event.getInviterId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "Team invitation";
    String content = String.format("%s invited you to join %s.", inviter.getName(), teamName);

    return NotificationTemplate.builder()
        .imageUrl(inviter.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.INVITATION)
        .subjectId(event.getInvitationId())
        .build();
  }

  @Override
  public NotificationTemplate getTeamDeletedTemplate(TeamDeletedEvent event) {
    UserSummaryProfile deletedBy = userService.getUserSummaryProfile(event.getDeletedBy());

    String title = "Team deleted";
    String content = String.format("%s deleted %s.", deletedBy.getName(), event.getTeamName());

    return NotificationTemplate.builder()
        .imageUrl(deletedBy.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TEAM)
        .subjectId(null)
        .build();
  }

  @Override
  public NotificationTemplate getTeamUpdatedTemplate(TeamUpdatedEvent event) {
    UserSummaryProfile updatedBy = userService.getUserSummaryProfile(event.getUpdatedBy());

    String title = "Team updated";
    String content =
        String.format(
            "%s updated the general information of %s", updatedBy.getName(), event.getTeamName());

    return NotificationTemplate.builder()
        .imageUrl(updatedBy.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TEAM)
        .subjectId(event.getTeamId())
        .build();
  }

  @Override
  public NotificationTemplate getUserJoinedTeamTemplate(UserJoinedTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "New team member";
    String content = String.format("%s joined %s.", user.getName(), teamName);

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TEAM)
        .subjectId(event.getTeamId())
        .build();
  }

  @Override
  public NotificationTemplate getUserLeftTeamTemplate(UserLeftTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    String title = "Member left";
    String content = String.format("%s left %s.", user.getName(), teamName);

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TEAM)
        .subjectId(event.getTeamId())
        .build();
  }

  @Override
  public NotificationTemplate getTaskAssignedTemplate(TaskAssignedEvent event) {
    UserSummaryProfile assigner = userService.getUserSummaryProfile(event.getAssignerId());

    String title = "New task assigned";
    String content =
        String.format("%s assigned task [%s] to you.", assigner.getName(), event.getTaskCode());

    return NotificationTemplate.builder()
        .imageUrl(assigner.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TASK)
        .subjectId(event.getTaskId())
        .build();
  }

  @Override
  public NotificationTemplate getTaskRemindedTemplate(TaskRemindedEvent event) {
    boolean isOverDueTask = event.getDueDate().isBefore(LocalDateTime.now());
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    UUID teamId = event.getTeamId();
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

    return NotificationTemplate.builder()
        .imageUrl(imageUrl)
        .title(title)
        .content(content)
        .subject(LinkedSubject.TASK)
        .subjectId(event.getTaskId())
        .build();
  }

  @Override
  public NotificationTemplate getTaskUpdatedTemplate(TaskUpdatedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = "Task updated";
    String content = String.format("%s updated task [%s].", user.getName(), event.getTaskCode());

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TASK)
        .subjectId(event.getTaskId())
        .build();
  }

  @Override
  public NotificationTemplate getPlanCompletedTemplate(PlanCompletedEvent event) {
    String title = "Plan completed";
    String content = String.format("Plan [%s] is completed.", event.getPlanCode());

    return NotificationTemplate.builder()
        .imageUrl(event.getTeamAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.PLAN)
        .subjectId(event.getPlanId())
        .build();
  }

  @Override
  public NotificationTemplate getTaskDeletedTemplate(TaskDeletedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = "Task deleted";
    String content = String.format("%s deleted task [%s].", user.getName(), event.getTaskCode());

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.TASK)
        .subjectId(event.getTaskId())
        .build();
  }

  @Override
  public NotificationTemplate getPlanDeletedTemplate(PlanDeletedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = "Plan deleted";
    String content = String.format("%s deleted plan [%s].", user.getName(), event.getPlanCode());

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.PLAN)
        .subjectId(event.getPlanId())
        .build();
  }

  @Override
  public NotificationTemplate getPlanUpdatedTemplate(PlanUpdatedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = "Plan updated";
    String content = String.format("%s updated plan [%s].", user.getName(), event.getPlanCode());

    return NotificationTemplate.builder()
        .imageUrl(user.getAvatarUrl())
        .title(title)
        .content(content)
        .subject(LinkedSubject.PLAN)
        .subjectId(event.getPlanId())
        .build();
  }

  @Override
  public NotificationTemplate getMessageSentTemplate(MessageSentEvent event) {
    UUID teamId = event.getTeamId();
    String teamAvatarUrl = teamService.getTeamAvatarUrl(teamId);
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    String title = teamService.getTeamName(teamId);
    String content = String.format("%s sent new message.", user.getName());

    return NotificationTemplate.builder()
        .imageUrl(teamAvatarUrl)
        .title(title)
        .content(content)
        .subject(LinkedSubject.TEAM)
        .subjectId(event.getTeamId())
        .build();
  }
}
