package com.study.studypal.plan.dto.recurrence.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.RecurrenceType;
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
public class TaskRecurrenceRuleResponseDto {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private RecurrenceType recurrenceType;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private List<DayOfWeek> weekDays;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate recurrenceStartDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate recurrenceEndDate;
}
