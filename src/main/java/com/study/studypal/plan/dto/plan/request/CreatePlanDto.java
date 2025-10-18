package com.study.studypal.plan.dto.plan.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreatePlanDto {
  @NotEmpty(message = "Title is required")
  @Size(max = 50, message = "Title must be less than 50 characters")
  private String title;

  private String description;

  @NotNull(message = "Start date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @NotNull(message = "Due date is required")
  @Future(message = "Due date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @NotNull(message = "Priority is required")
  private Priority priority;
}
