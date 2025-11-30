package com.study.studypal.plan.dto.task.internal;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskInfo {
  private String content;
  private LocalDateTime startDate;
  private LocalDateTime dueDate;
}
