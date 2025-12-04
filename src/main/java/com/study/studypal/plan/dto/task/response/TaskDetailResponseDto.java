package com.study.studypal.plan.dto.task.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import com.study.studypal.plan.enums.TaskType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDetailResponseDto {
  private UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private TaskType taskType;

  private String taskCode;

  private String content;

  private String note;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime completeDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;

  private TaskAdditionalDataResponseDto additionalData;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime deletedAt;
}
