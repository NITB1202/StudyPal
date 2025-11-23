package com.study.studypal.plan.dto.plan.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
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

  private String planCode;

  private String title;

  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime dueDate;

  private Integer totalTasksCount;

  private Integer completedTaskCount;

  List<TaskResponseDto> tasks;
}
