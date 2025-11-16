package com.study.studypal.plan.dto.plan.internal;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanInfo {
  private UUID id;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;
}
