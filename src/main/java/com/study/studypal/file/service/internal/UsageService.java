package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.TeamUsage;
import com.study.studypal.file.entity.UserUsage;
import java.util.UUID;

public interface UsageService {
  void createUserUsage(UUID userId);

  void createTeamUsage(UUID teamId);

  UserUsage getUserUsage(UUID userId);

  TeamUsage getTeamUsage(UUID teamId);
}
