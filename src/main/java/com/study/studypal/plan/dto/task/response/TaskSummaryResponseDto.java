package com.study.studypal.plan.dto.task.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import com.study.studypal.plan.enums.TaskType;
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
public class TaskSummaryResponseDto {
  private UUID id;

  private String content;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private TaskType taskType;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime completeDate;
}
