package com.study.studypal.plan.dto.plan.internal;

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
public class PlanInfo {
  private UUID assignerId;

  private UUID teamId;

  private UUID planId;
}
