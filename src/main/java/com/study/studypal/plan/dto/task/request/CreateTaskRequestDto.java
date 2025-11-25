package com.study.studypal.plan.dto.task.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateTaskRequestDto {
  @NotEmpty(message = "Content is required")
  private String content;

  @NotNull(message = "Start date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @NotNull(message = "Due date is required")
  @Future(message = "Due date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  private Priority priority;

  private String note;
}
