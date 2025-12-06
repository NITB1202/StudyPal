package com.study.studypal.plan.dto.task.response;

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
public class ListTaskResponseDto {
  private List<TaskSummaryResponseDto> tasks;

  private long total;

  private String nextCursor;
}
