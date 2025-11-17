package com.study.studypal.plan.dto.task.internal;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskInfo {
  private UUID id;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;
}
