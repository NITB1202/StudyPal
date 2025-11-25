package com.study.studypal.plan.dto.task.internal;

import com.study.studypal.plan.enums.Priority;
import java.time.LocalDateTime;
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
public class CreateTaskInfo {
  private String content;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;

  private String note;

  private Priority priority;
}
