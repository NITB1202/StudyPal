package com.study.studypal.plan.service.internal;

import java.util.UUID;

public interface PlanHistoryInternalService {
  void logCreatePlan(UUID userId, UUID planId);

  void logAssignTask(UUID assignerId, UUID assigneeId, UUID planId, String taskCode);
}
