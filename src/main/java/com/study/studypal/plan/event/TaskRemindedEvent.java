package com.study.studypal.plan.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskRemindedEvent {
  private UUID taskId;
  private String taskCode;
  private String taskContent;
  private LocalDateTime dueDate;
}

// now < dueDate
// Message: Your task will expire at 10:00:00 on 12-02-2025
// [TSK-00001] Research school history. -> Linked: taskId

// else
// Message: Your task is overdue
// [TSK-00001] Research school history. -> Linked: taskId
