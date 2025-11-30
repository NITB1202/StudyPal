package com.study.studypal.plan.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskUpdatedEvent {
  private UUID userId;
  private UUID assigneeId;
  private UUID taskId;
  private String taskCode;
}

// Message: User01 updated task [TSK-00001]. -> Linked: taskId
