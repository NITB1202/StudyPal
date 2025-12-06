package com.study.studypal.plan.dto.plan.response;

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
public class ListPlanResponseDto {
  private List<PlanSummaryResponseDto> plans;

  private long total;

  private String nextCursor;
}
