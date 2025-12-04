package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Plan;
import java.util.Set;
import java.util.UUID;

public interface PlanInternalService {
  UUID getTeamIdById(UUID id);

  Set<UUID> getPlanRelatedMemberIds(UUID planId);

  float updatePlanProgress(UUID id);

  void deletePlan(Plan plan);
}
