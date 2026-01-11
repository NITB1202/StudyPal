package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import java.util.Set;
import java.util.UUID;

public interface TaskNotificationService {
  void publishTaskAssignedNotification(UUID assignerId, Task task);

  void publishTaskRemindedNotification(Task task);

  void publishTaskUpdatedNotification(UUID userId, Task task);

  void publishTaskDeletedNotification(UUID userId, Task task);

  void publishPlanCompletedNotification(Plan plan);

  void publishPlanDeletedNotification(UUID userId, Plan plan, Set<UUID> relatedMemberIds);

  void publishPlanUpdatedNotification(UUID userId, Plan plan);

  void publishTaskCompletedNotification(UUID userId, Task task);
}
