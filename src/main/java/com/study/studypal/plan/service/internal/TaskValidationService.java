package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.task.internal.UpdateTaskInfo;
import com.study.studypal.plan.entity.Task;
import java.util.UUID;

public interface TaskValidationService {
  void validateViewTaskPermission(UUID userId, Task task);

  void validateUpdateTaskPermission(UUID userId, Task task);

  void validateTaskOwnership(UUID userId, Task task);

  void validatePersonalTask(Task task);

  void validateTeamTask(Task task);

  void validateUpdateTaskRequest(Task task, UpdateTaskInfo info);
}
