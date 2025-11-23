package com.study.studypal.plan.service.internal;

import java.util.UUID;

public interface PlanInternalService {
  UUID getTeamIdById(UUID id);

  void updatePlanProgress(UUID id);
}
