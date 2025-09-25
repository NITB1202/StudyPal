package com.study.studypal.plan.dto.recurrence.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
public class CreatePlanRecurrenceRuleDto {
  @NotNull(message = "Week days are required")
  @Size(min = 1, message = "The list must contain at least 1 item")
  private List<DayOfWeek> weekDays;

  @NotNull(message = "Recurrence start date is required")
  private LocalDate recurrenceStartDate;

  @NotNull(message = "Recurrence end date is required")
  private LocalDate recurrenceEndDate;
}
