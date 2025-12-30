package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Task;
import java.time.LocalDateTime;

public interface TaskRecurrenceRuleInternalService {
  void validateClonedTaskDuration(LocalDateTime startDate, LocalDateTime dueDate);

  boolean isRootOrClonedTask(Task task);

  boolean isRootTask(Task task);

  void updateRootTask(Task oldTask, Task newTask);
}
