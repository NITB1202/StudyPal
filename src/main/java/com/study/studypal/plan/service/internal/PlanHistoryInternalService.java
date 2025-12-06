package com.study.studypal.plan.service.internal;

import java.util.UUID;

public interface PlanHistoryInternalService {
  void logCreatePlan(UUID userId, UUID planId);

  void logAssignTask(UUID assignerId, UUID assigneeId, UUID planId, String taskCode);

  void logUpdateTask(UUID userId, UUID planId, String taskCode);

  void logCompleteTask(UUID userId, UUID planId, String taskCode);

  void logDeleteTask(UUID userId, UUID planId, String taskCode);

  void logDeletePlan(UUID userId, UUID planId);

  void logUpdatePlan(UUID userId, UUID planId);

  void logRecoverPlan(UUID userId, UUID planId);

  void logRecoverTask(UUID userId, UUID planId, String taskCode);
}
