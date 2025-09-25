package com.study.studypal.plan.dto.plan.request;

import com.study.studypal.plan.dto.task.request.CreateTaskForTeamPlanDto;
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
public class CreateTeamPlanRequestDto {
  @NotNull(message = "Team id is required")
  private UUID teamId;

  @NotNull(message = "Plan is required")
  private CreatePlanDto plan;

  @NotNull(message = "Tasks are required")
  @Size(min = 1, message = "The list must contain at least 1 item")
  private List<CreateTaskForTeamPlanDto> tasks;
}
