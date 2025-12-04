package com.study.studypal.plan.event.plan;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanDeletedEvent {
  private UUID userId;
  private UUID planId;
  private String planCode;
}

// Message: User01 deleted plan [PLN-00001]. -> Linked: planId
