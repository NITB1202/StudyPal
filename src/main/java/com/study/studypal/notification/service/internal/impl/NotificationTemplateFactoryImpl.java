package com.study.studypal.notification.service.internal.impl;

import static com.study.studypal.notification.constant.NotificationConstant.DATA_KEY_DATE;
import static com.study.studypal.notification.constant.NotificationConstant.DATA_KEY_RESOURCE;
import static com.study.studypal.notification.constant.NotificationConstant.DATA_KEY_SUBJECT;
import static com.study.studypal.notification.constant.NotificationConstant.DATA_KEY_TIME;
import static com.study.studypal.notification.constant.NotificationConstant.DATE_FORMAT;
import static com.study.studypal.notification.constant.NotificationConstant.TIME_FORMAT;

import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.chat.event.UserMentionedEvent;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import com.study.studypal.notification.entity.NotificationDefinition;
import com.study.studypal.notification.enums.NotificationDefinitionCode;
import com.study.studypal.notification.exception.NotificationErrorCode;
import com.study.studypal.notification.repository.NotificationDefinitionRepository;
import com.study.studypal.notification.service.internal.NotificationTemplateFactory;
import com.study.studypal.plan.event.plan.PlanCompletedEvent;
import com.study.studypal.plan.event.plan.PlanDeletedEvent;
import com.study.studypal.plan.event.plan.PlanUpdatedEvent;
import com.study.studypal.plan.event.task.TaskAssignedEvent;
import com.study.studypal.plan.event.task.TaskCompletedEvent;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateFactoryImpl implements NotificationTemplateFactory {
  private final NotificationDefinitionRepository definitionRepository;
  private final UserInternalService userService;
  private final TeamInternalService teamService;

  @Override
  public NotificationTemplate getInvitationCreatedTemplate(InvitationCreatedEvent event) {
    UserSummaryProfile inviter = userService.getUserSummaryProfile(event.getInviterId());
    String teamName = teamService.getTeamName(event.getTeamId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, inviter.getName());
    params.put(DATA_KEY_RESOURCE, teamName);

    return buildNotificationTemplate(
        NotificationDefinitionCode.INVITATION_CREATED,
        params,
        inviter.getAvatarUrl(),
        event.getInvitationId());
  }

  @Override
  public NotificationTemplate getTeamDeletedTemplate(TeamDeletedEvent event) {
    UserSummaryProfile deletedBy = userService.getUserSummaryProfile(event.getDeletedBy());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, deletedBy.getName());
    params.put(DATA_KEY_RESOURCE, event.getTeamName());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TEAM_DELETED, params, deletedBy.getAvatarUrl(), null);
  }

  @Override
  public NotificationTemplate getTeamUpdatedTemplate(TeamUpdatedEvent event) {
    UserSummaryProfile updatedBy = userService.getUserSummaryProfile(event.getUpdatedBy());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, updatedBy.getName());
    params.put(DATA_KEY_RESOURCE, event.getTeamName());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TEAM_UPDATED,
        params,
        updatedBy.getAvatarUrl(),
        event.getTeamId());
  }

  @Override
  public NotificationTemplate getUserJoinedTeamTemplate(UserJoinedTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, teamName);

    return buildNotificationTemplate(
        NotificationDefinitionCode.TEAM_JOINED, params, user.getAvatarUrl(), event.getTeamId());
  }

  @Override
  public NotificationTemplate getUserLeftTeamTemplate(UserLeftTeamEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, teamName);

    return buildNotificationTemplate(
        NotificationDefinitionCode.TEAM_LEFT, params, user.getAvatarUrl(), event.getTeamId());
  }

  @Override
  public NotificationTemplate getTaskAssignedTemplate(TaskAssignedEvent event) {
    UserSummaryProfile assigner = userService.getUserSummaryProfile(event.getAssignerId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, assigner.getName());
    params.put(DATA_KEY_RESOURCE, event.getTaskCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TASK_ASSIGNED,
        params,
        assigner.getAvatarUrl(),
        event.getTaskId());
  }

  @Override
  public NotificationTemplate getTaskRemindedTemplate(TaskRemindedEvent event) {
    boolean isOverDueTask = event.getDueDate().isBefore(LocalDateTime.now());
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    UUID teamId = event.getTeamId();
    String imageUrl = teamId != null ? teamService.getTeamAvatarUrl(teamId) : null;

    NotificationDefinitionCode code =
        isOverDueTask
            ? NotificationDefinitionCode.TASK_EXPIRED
            : NotificationDefinitionCode.TASK_REMINDED;

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, event.getTaskCode());

    if (!isOverDueTask) {
      params.put(DATA_KEY_TIME, event.getDueDate().format(timeFormatter));
      params.put(DATA_KEY_DATE, event.getDueDate().format(dateFormatter));
    }

    return buildNotificationTemplate(code, params, imageUrl, event.getTaskId());
  }

  @Override
  public NotificationTemplate getTaskUpdatedTemplate(TaskUpdatedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, event.getTaskCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TASK_UPDATED, params, user.getAvatarUrl(), event.getTaskId());
  }

  @Override
  public NotificationTemplate getPlanCompletedTemplate(PlanCompletedEvent event) {
    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, event.getPlanCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.PLAN_COMPLETED,
        params,
        event.getTeamAvatarUrl(),
        event.getPlanId());
  }

  @Override
  public NotificationTemplate getTaskDeletedTemplate(TaskDeletedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, event.getTaskCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TASK_DELETED, params, user.getAvatarUrl(), event.getTaskId());
  }

  @Override
  public NotificationTemplate getPlanDeletedTemplate(PlanDeletedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, event.getPlanCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.PLAN_DELETED, params, user.getAvatarUrl(), event.getPlanId());
  }

  @Override
  public NotificationTemplate getPlanUpdatedTemplate(PlanUpdatedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, event.getPlanCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.PLAN_UPDATED, params, user.getAvatarUrl(), event.getPlanId());
  }

  @Override
  public NotificationTemplate getMessageSentTemplate(MessageSentEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, teamName);

    return buildNotificationTemplate(
        NotificationDefinitionCode.MESSAGE_SENT, params, user.getAvatarUrl(), event.getTeamId());
  }

  @Override
  public NotificationTemplate getTaskCompletedTemplate(TaskCompletedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, event.getTaskCode());

    return buildNotificationTemplate(
        NotificationDefinitionCode.TASK_COMPLETED, params, user.getAvatarUrl(), event.getTaskId());
  }

  @Override
  public NotificationTemplate getUserMentionedTemplate(UserMentionedEvent event) {
    UserSummaryProfile user = userService.getUserSummaryProfile(event.getUserId());
    String teamName = teamService.getTeamName(event.getTeamId());

    Map<String, String> params = new HashMap<>();
    params.put(DATA_KEY_SUBJECT, user.getName());
    params.put(DATA_KEY_RESOURCE, teamName);

    return buildNotificationTemplate(
        NotificationDefinitionCode.USER_MENTIONED, params, user.getAvatarUrl(), event.getTeamId());
  }

  private NotificationDefinition getByCode(NotificationDefinitionCode code) {
    return definitionRepository
        .findByCode(code.name())
        .orElseThrow(
            () -> new BaseException(NotificationErrorCode.NOTIFICATION_DEFINITION_NOT_FOUND, code));
  }

  private String fillBody(String body, Map<String, String> values) {
    String result = body;
    for (Map.Entry<String, String> entry : values.entrySet()) {
      result = result.replace(entry.getKey(), entry.getValue());
    }
    return result;
  }

  private NotificationTemplate buildNotificationTemplate(
      NotificationDefinitionCode code,
      Map<String, String> params,
      String imageUrl,
      UUID subjectId) {
    NotificationDefinition definition = getByCode(code);
    String body = fillBody(definition.getBody(), params);

    return NotificationTemplate.builder()
        .imageUrl(imageUrl)
        .title(definition.getTitle())
        .content(body)
        .subject(definition.getSubject())
        .subjectId(subjectId)
        .build();
  }
}
