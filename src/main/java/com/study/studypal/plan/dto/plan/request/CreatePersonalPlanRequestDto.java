package com.study.studypal.plan.dto.plan.request;

import com.study.studypal.plan.dto.recurrence.request.CreatePlanRecurrenceRuleDto;
import com.study.studypal.plan.dto.task.request.CreateTaskForPersonalPlanDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
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
public class CreatePersonalPlanRequestDto {
  @NotNull(message = "Plan is required")
  private CreatePlanDto plan;

  private CreatePlanRecurrenceRuleDto recurrenceRule;

  @NotNull(message = "Tasks are required")
  @Size(min = 1, message = "The list must contain at least 1 item")
  private List<CreateTaskForPersonalPlanDto> tasks;

  private List<LocalDateTime> reminderTimes;
}
