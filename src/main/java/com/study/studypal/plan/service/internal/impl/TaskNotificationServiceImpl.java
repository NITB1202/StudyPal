package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.event.plan.PlanCompletedEvent;
import com.study.studypal.plan.event.plan.PlanDeletedEvent;
import com.study.studypal.plan.event.task.TaskAssignedEvent;
import com.study.studypal.plan.event.task.TaskDeletedEvent;
import com.study.studypal.plan.event.task.TaskRemindedEvent;
import com.study.studypal.plan.event.task.TaskUpdatedEvent;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.team.entity.Team;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskNotificationServiceImpl implements TaskNotificationService {
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void publishTaskAssignedNotification(UUID assignerId, Task task) {
    TaskAssignedEvent event =
        TaskAssignedEvent.builder()
            .assignerId(assignerId)
            .taskId(task.getId())
            .taskCode(task.getTaskCode())
            .assigneeId(task.getAssignee().getId())
            .build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishTaskRemindedNotification(Task task) {
    UUID teamId =
        Optional.ofNullable(task.getPlan()).map(Plan::getTeam).map(Team::getId).orElse(null);

    TaskRemindedEvent event =
        TaskRemindedEvent.builder()
            .taskId(task.getId())
            .userId(task.getAssignee().getId())
            .teamId(teamId)
            .taskCode(task.getTaskCode())
            .dueDate(task.getDueDate())
            .build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishTaskUpdatedNotification(UUID userId, Task task) {
    TaskUpdatedEvent event =
        TaskUpdatedEvent.builder()
            .userId(userId)
            .assigneeId(task.getAssignee().getId())
            .taskId(task.getId())
            .taskCode(task.getTaskCode())
            .build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishTaskDeletedNotification(UUID userId, Task task) {
    TaskDeletedEvent event =
        TaskDeletedEvent.builder()
            .userId(userId)
            .assigneeId(task.getAssignee().getId())
            .taskId(task.getId())
            .taskCode(task.getTaskCode())
            .build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishPlanCompletedNotification(Plan plan) {
    PlanCompletedEvent event =
        PlanCompletedEvent.builder()
            .planId(plan.getId())
            .planCode(plan.getPlanCode())
            .teamAvatarUrl(plan.getTeam().getAvatarUrl())
            .build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishPlanDeletedNotification(UUID userId, Plan plan) {
    PlanDeletedEvent event =
        PlanDeletedEvent.builder()
            .userId(userId)
            .planId(plan.getId())
            .planCode(plan.getPlanCode())
            .build();

    eventPublisher.publishEvent(event);
  }
}
