package com.study.studypal.plan.dto.task.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDetailResponseDto {
  private UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;

  private String content;

  private String taskCode;

  private String note;

  private UUID assigneeId;

  private String assigneeAvatarUrl;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime completeDate;
}
