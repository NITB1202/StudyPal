package com.study.studypal.plan.service.internal;

import java.util.Set;
import java.util.UUID;

public interface PlanInternalService {
  UUID getTeamIdById(UUID id);

  Set<UUID> getPlanRelatedMemberIds(UUID planId);

  float updatePlanProgress(UUID id);

  void deleteById(UUID id);
}
