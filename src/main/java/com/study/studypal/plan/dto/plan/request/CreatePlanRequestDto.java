package com.study.studypal.plan.dto.plan.request;

import com.study.studypal.plan.dto.task.request.CreateTaskForPlanDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
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
public class CreatePlanRequestDto {
  @NotNull(message = "Team id is required")
  private UUID teamId;

  @NotBlank(message = "Title is required")
  @Size(max = 50, message = "Title must be less than 50 characters")
  private String title;

  private String description;

  @NotNull(message = "Tasks are required")
  @Size(min = 1, message = "The list must contain at least 1 item")
  private List<CreateTaskForPlanDto> tasks;
}
