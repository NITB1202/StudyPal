package com.study.studypal.plan.service.internal;

import java.util.UUID;

public interface TaskCounterService {
  void createUserTaskCounter(UUID userId);

  void createTeamTaskCounter(UUID teamId);
}
