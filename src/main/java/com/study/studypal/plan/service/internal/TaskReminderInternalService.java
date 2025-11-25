package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Task;
import java.time.LocalDateTime;

public interface TaskReminderInternalService {
  void scheduleReminder(LocalDateTime remindAt, Task task);
}
