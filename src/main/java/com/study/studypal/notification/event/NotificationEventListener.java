package com.study.studypal.notification.event;

import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.chat.service.internal.ChatNotificationService;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import com.study.studypal.notification.service.internal.NotificationTemplateFactory;
import com.study.studypal.notification.service.internal.NotificationWebSocketHandler;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.plan.event.plan.PlanCompletedEvent;
import com.study.studypal.plan.event.plan.PlanDeletedEvent;
import com.study.studypal.plan.event.plan.PlanUpdatedEvent;
import com.study.studypal.plan.event.task.TaskAssignedEvent;
import com.study.studypal.plan.event.task.TaskCompletedEvent;
import com.study.studypal.plan.event.task.TaskDeletedEvent;
import com.study.studypal.plan.event.task.TaskRemindedEvent;
import com.study.studypal.plan.event.task.TaskUpdatedEvent;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.team.event.invitation.InvitationCreatedEvent;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.event.team.UserJoinedTeamEvent;
import com.study.studypal.team.event.team.UserLeftTeamEvent;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
  private final DeviceTokenInternalService deviceTokenService;
  private final NotificationInternalService notificationService;
  private final TeamNotificationSettingInternalService settingService;
  private final PlanInternalService planService;
  private final ChatNotificationService chatService;
  private final NotificationWebSocketHandler webSocketHandler;
  private final NotificationTemplateFactory templateFactory;
  private final TeamMembershipInternalService memberService;

  @Async
  @EventListener
  public void handleInvitationCreatedEvent(InvitationCreatedEvent event) {
    NotificationTemplate template = templateFactory.getInvitationCreatedTemplate(event);
    List<UUID> recipients = List.of(event.getInviteeId());
    webSocketHandler.sendNotificationToOnlineUsers(recipients, template);
    deviceTokenService.sendPushNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTeamDeletedEvent(TeamDeletedEvent event) {
    NotificationTemplate template = templateFactory.getTeamDeletedTemplate(event);
    List<UUID> recipients = event.getMemberIds();
    recipients.remove(event.getDeletedBy());
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTeamUpdatedEvent(TeamUpdatedEvent event) {
    NotificationTemplate template = templateFactory.getTeamUpdatedTemplate(event);
    List<UUID> memberIds = memberService.getMemberIds(event.getTeamId());
    List<UUID> recipients =
        getTeamNotificationEnabledRecipients(event.getTeamId(), event.getUpdatedBy(), memberIds);
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleUserJoinedTeamEvent(UserJoinedTeamEvent event) {
    NotificationTemplate template = templateFactory.getUserJoinedTeamTemplate(event);
    List<UUID> memberIds = memberService.getMemberIds(event.getTeamId());
    List<UUID> recipients =
        getTeamNotificationEnabledRecipients(event.getTeamId(), event.getUserId(), memberIds);
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleUserLeftTeamEvent(UserLeftTeamEvent event) {
    NotificationTemplate template = templateFactory.getUserLeftTeamTemplate(event);
    List<UUID> memberIds = memberService.getMemberIds(event.getTeamId());
    List<UUID> recipients =
        getTeamNotificationEnabledRecipients(event.getTeamId(), event.getUserId(), memberIds);
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTaskAssignedEvent(TaskAssignedEvent event) {
    if (event.getAssignerId().equals(event.getAssigneeId())) {
      return;
    }

    NotificationTemplate template = templateFactory.getTaskAssignedTemplate(event);
    List<UUID> recipients = List.of(event.getAssigneeId());
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTaskRemindedEvent(TaskRemindedEvent event) {
    UUID userId = event.getUserId();
    UUID teamId = event.getTeamId();

    if (teamId != null && !settingService.getTeamPlanReminderSetting(userId, teamId)) {
      return;
    }

    NotificationTemplate template = templateFactory.getTaskRemindedTemplate(event);
    List<UUID> recipients = List.of(userId);
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTaskUpdatedEvent(TaskUpdatedEvent event) {
    if (event.getUserId().equals(event.getAssigneeId())) {
      return;
    }

    NotificationTemplate template = templateFactory.getTaskUpdatedTemplate(event);
    List<UUID> recipients = List.of(event.getAssigneeId());
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handlePlanCompletedEvent(PlanCompletedEvent event) {
    NotificationTemplate template = templateFactory.getPlanCompletedTemplate(event);

    UUID teamId = planService.getTeamIdById(event.getPlanId());
    Set<UUID> relatedMemberIds = planService.getPlanRelatedMemberIds(event.getPlanId());
    List<UUID> recipients =
        getTeamPlanNotificationEnabledRecipients(teamId, null, relatedMemberIds);

    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTaskDeletedEvent(TaskDeletedEvent event) {
    if (event.getUserId().equals(event.getAssigneeId())) {
      return;
    }

    NotificationTemplate template = templateFactory.getTaskDeletedTemplate(event);
    List<UUID> recipients = List.of(event.getAssigneeId());
    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handlePlanDeletedEvent(PlanDeletedEvent event) {
    NotificationTemplate template = templateFactory.getPlanDeletedTemplate(event);

    UUID teamId = planService.getTeamIdById(event.getPlanId());
    List<UUID> recipients =
        getTeamPlanNotificationEnabledRecipients(
            teamId, event.getUserId(), event.getRelatedMemberIds());

    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handlePlanUpdatedEvent(PlanUpdatedEvent event) {
    NotificationTemplate template = templateFactory.getPlanUpdatedTemplate(event);

    UUID teamId = planService.getTeamIdById(event.getPlanId());
    Set<UUID> relatedMemberIds = planService.getPlanRelatedMemberIds(event.getPlanId());
    List<UUID> recipients =
        getTeamPlanNotificationEnabledRecipients(teamId, event.getUserId(), relatedMemberIds);

    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleMessageSentEvent(MessageSentEvent event) {
    NotificationTemplate template = templateFactory.getMessageSentTemplate(event);

    UUID teamId = event.getTeamId();
    List<UUID> relatedMemberIds = chatService.getOfflineMemberIds(teamId);
    List<UUID> recipients =
        getTeamChatNotificationEnabledRecipients(teamId, event.getUserId(), relatedMemberIds);

    processNotification(recipients, template);
  }

  @Async
  @EventListener
  public void handleTaskCompletedEvent(TaskCompletedEvent event) {
    NotificationTemplate template = templateFactory.getTaskCompletedTemplate(event);

    UUID teamId = planService.getTeamIdById(event.getPlanId());
    Set<UUID> relatedMemberIds = planService.getPlanRelatedMemberIds(event.getPlanId());
    List<UUID> recipients =
        getTeamPlanNotificationEnabledRecipients(teamId, event.getUserId(), relatedMemberIds);

    processNotification(recipients, template);
  }

  private List<UUID> getTeamNotificationEnabledRecipients(
      UUID teamId, UUID senderId, List<UUID> memberIds) {
    return memberIds.stream()
        .filter(memberId -> !memberId.equals(senderId))
        .filter(memberId -> settingService.getTeamNotificationSetting(memberId, teamId))
        .toList();
  }

  private List<UUID> getTeamPlanNotificationEnabledRecipients(
      UUID teamId, UUID senderId, Set<UUID> memberIds) {
    return memberIds.stream()
        .filter(memberId -> !memberId.equals(senderId))
        .filter(memberId -> settingService.getTeamPlanReminderSetting(memberId, teamId))
        .toList();
  }

  private List<UUID> getTeamChatNotificationEnabledRecipients(
      UUID teamId, UUID senderId, List<UUID> memberIds) {
    return memberIds.stream()
        .filter(memberId -> !memberId.equals(senderId))
        .filter(memberId -> settingService.getChatNotificationSetting(memberId, teamId))
        .toList();
  }

  private void processNotification(List<UUID> recipients, NotificationTemplate template) {
    webSocketHandler.sendNotificationToOnlineUsers(recipients, template);
    notificationService.createNotification(recipients, template);
    notificationService.evictNotificationCaches(recipients);
    deviceTokenService.sendPushNotification(recipients, template);
  }
}
