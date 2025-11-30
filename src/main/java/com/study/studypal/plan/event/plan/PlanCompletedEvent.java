package com.study.studypal.plan.event.plan;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanCompletedEvent {
  private UUID creatorId;
  private UUID planId;
  private String planCode;
  private String teamAvatarUrl;
}
