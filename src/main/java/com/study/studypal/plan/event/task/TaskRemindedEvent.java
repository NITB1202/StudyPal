package com.study.studypal.plan.event.task;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskRemindedEvent {
  private UUID taskId;
  private UUID teamId;
  private UUID userId;
  private String taskCode;
  private LocalDateTime dueDate;
}

// now < dueDate
// Message: Task [TSK-00001] will expire at 10:00:00 on 12-02-2025 -> Linked: taskId

// else
// Message: Task [TSK-00001] is overdue.-> Linked: taskId
