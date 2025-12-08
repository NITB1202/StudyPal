package com.study.studypal.plan.dto.statistic.response;

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
public class ListTaskStatisticsResponseDto {
  private List<TaskStatisticsResponseDto> statistics;

  private long total;

  private String nextCursor;
}
