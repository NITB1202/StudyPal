package com.study.studypal.plan.dto.task.internal;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateTasksInfo {
  private UUID planId;
  private LocalDateTime planStartDate;
  private LocalDateTime planDueDate;
}
