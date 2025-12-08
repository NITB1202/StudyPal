package com.study.studypal.plan.dto.statistic.response;

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
public class TaskDetailStatisticsResponseDto {
  private long total;

  private long unfinished;

  private long low;

  private long medium;

  private long high;
}
