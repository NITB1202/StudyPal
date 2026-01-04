package com.study.studypal.notification.service.internal;

import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
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

public interface NotificationTemplateFactory {
  NotificationTemplate getInvitationCreatedTemplate(InvitationCreatedEvent event);

  NotificationTemplate getTeamDeletedTemplate(TeamDeletedEvent event);

  NotificationTemplate getTeamUpdatedTemplate(TeamUpdatedEvent event);

  NotificationTemplate getUserJoinedTeamTemplate(UserJoinedTeamEvent event);

  NotificationTemplate getUserLeftTeamTemplate(UserLeftTeamEvent event);

  NotificationTemplate getTaskAssignedTemplate(TaskAssignedEvent event);

  NotificationTemplate getTaskRemindedTemplate(TaskRemindedEvent event);

  NotificationTemplate getTaskUpdatedTemplate(TaskUpdatedEvent event);

  NotificationTemplate getPlanCompletedTemplate(PlanCompletedEvent event);

  NotificationTemplate getTaskDeletedTemplate(TaskDeletedEvent event);

  NotificationTemplate getPlanDeletedTemplate(PlanDeletedEvent event);

  NotificationTemplate getPlanUpdatedTemplate(PlanUpdatedEvent event);

  NotificationTemplate getMessageSentTemplate(MessageSentEvent event);
}
