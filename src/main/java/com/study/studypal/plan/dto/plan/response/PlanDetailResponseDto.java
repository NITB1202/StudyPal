package com.study.studypal.plan.dto.plan.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.study.studypal.plan.converter.LocalDateTimeListSerializer;
import com.study.studypal.plan.dto.plancomment.response.PlanCommentResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.enums.Priority;
import java.time.LocalDateTime;
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
public class PlanDetailResponseDto {
  private UUID id;

  private UUID parentPlanId;

  private UUID teamId;

  private String title;

  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;

  @JsonSerialize(using = LocalDateTimeListSerializer.class)
  List<LocalDateTime> reminders;

  List<TaskResponseDto> tasks;

  List<PlanCommentResponseDto> comments;
}
