package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.event.TaskAssignedEvent;
import com.study.studypal.plan.event.TaskRemindedEvent;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.team.entity.Team;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
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
}
