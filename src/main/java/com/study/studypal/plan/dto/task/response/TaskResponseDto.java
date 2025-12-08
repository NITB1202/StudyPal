package com.study.studypal.plan.dto.task.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
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
public class TaskResponseDto {
  private UUID id;

  private String content;

  private UUID assigneeId;

  private String assigneeName;

  private String assigneeAvatarUrl;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime completedAt;
}
