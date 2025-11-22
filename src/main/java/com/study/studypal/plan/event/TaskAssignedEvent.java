package com.study.studypal.plan.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskAssignedEvent {
  private UUID assignerId;
  private UUID taskId;
  private String taskCode;
  private String taskContent;
  private UUID assigneeId;
}

// Message: User01 assigned a task to you
// [TSK-00001] Research school history. -> Linked: taskId
