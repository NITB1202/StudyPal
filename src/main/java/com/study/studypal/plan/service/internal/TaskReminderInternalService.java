package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.task.internal.TaskInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskReminderInternalService {
  void createReminders(TaskInfo taskInfo, List<LocalDateTime> reminders);

  List<LocalDateTime> getAll(UUID planId);
}
