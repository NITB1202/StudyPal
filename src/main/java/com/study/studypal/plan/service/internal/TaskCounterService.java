package com.study.studypal.plan.service.internal;

import java.util.UUID;

public interface TaskCounterService {
  void createUserTaskCounter(UUID userId);

  void createTeamTaskCounter(UUID teamId);

  long increaseTeamTaskCounter(UUID teamId);

  long increaseUserTaskCounter(UUID userId);

  long getCurrentUserTaskCounter(UUID userId);

  void updateUserTaskCounter(UUID userId, long counter);
}
