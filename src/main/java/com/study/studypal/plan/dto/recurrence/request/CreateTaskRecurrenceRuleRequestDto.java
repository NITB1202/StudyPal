package com.study.studypal.plan.dto.recurrence.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.RecurrenceType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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
public class CreateTaskRecurrenceRuleRequestDto {
  @NotNull(message = "Recurrence type is required")
  private RecurrenceType type;

  private List<DayOfWeek> weekDays;

  @Future(message = "Recurrence start date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate recurrenceStartDate;

  @NotNull(message = "Recurrence end date is required")
  @Future(message = "Recurrence end date must be in the future")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate recurrenceEndDate;
}
