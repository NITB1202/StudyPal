package com.study.studypal.plan.dto.task.request;

import jakarta.validation.constraints.NotEmpty;
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
public class CreateTaskForPersonalPlanDto {
  @NotEmpty(message = "Name is required")
  private String name;

  private LocalDateTime dueDate;
}
