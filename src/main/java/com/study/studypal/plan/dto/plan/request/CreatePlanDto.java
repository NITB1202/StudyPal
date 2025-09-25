package com.study.studypal.plan.dto.plan.request;

import com.study.studypal.plan.enums.Priority;
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
public class CreatePlanDto {
  @NotEmpty(message = "Name is required")
  private String name;

  private String note;

  @NotNull(message = "Start date is required")
  private LocalDateTime startDate;

  @NotNull(message = "Due date is required")
  private LocalDateTime dueDate;

  @NotNull(message = "Priority is required")
  private Priority priority;
}
