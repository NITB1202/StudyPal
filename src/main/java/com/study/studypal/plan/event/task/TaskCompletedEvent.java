package com.study.studypal.plan.event.task;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskCompletedEvent {
  private UUID userId;
  private UUID planId;
  private UUID taskId;
  private String taskCode;
}

// Message: User01 completed task [TSK-00001]-> Linked: taskId
