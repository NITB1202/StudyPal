package com.study.studypal.session.dto.session.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionStatisticsResponseDto {
  private Long timeSpentInSeconds;

  private Double completionPercentage;
}
