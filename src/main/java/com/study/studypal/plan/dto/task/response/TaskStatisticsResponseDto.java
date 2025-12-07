package com.study.studypal.plan.dto.task.response;

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
public class TaskStatisticsResponseDto {
  private UUID userId;

  private String name;

  private String avatarUrl;

  private Long completedTaskCount;
}
