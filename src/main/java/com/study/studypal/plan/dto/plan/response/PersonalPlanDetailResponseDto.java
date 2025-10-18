package com.study.studypal.plan.dto.plan.response;

import com.study.studypal.plan.dto.task.response.PersonalTaskResponseDto;
import com.study.studypal.plan.enums.Priority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalPlanDetailResponseDto {
  private UUID id;

  private String title;

  private String description;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  List<LocalDateTime> reminders;

  List<PersonalTaskResponseDto> tasks;
}
