package com.study.studypal.plan.dto.plan.response;

import com.study.studypal.plan.dto.task.response.TaskResponseDto;
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

  List<TaskResponseDto> tasks;
}
