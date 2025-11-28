package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Task;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TaskReminderInternalService {
  void scheduleReminder(LocalDateTime remindAt, Task task);

  void rescheduleDueDateReminder(LocalDateTime newDueDate, Task task);

  void deleteUsedReminder(UUID reminderId);

  void deleteInvalidReminders(UUID taskId, LocalDateTime newStartDate, LocalDateTime newDueDate);

  void deleteAllRemindersForTask(UUID taskId);
}
