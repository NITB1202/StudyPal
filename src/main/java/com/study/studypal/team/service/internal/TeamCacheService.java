package com.study.studypal.team.service.internal;

import java.util.List;
import java.util.UUID;

public interface TeamCacheService {
  void evictTeamDashboardCaches(UUID teamId, List<UUID> memberIds);

  void evictUserJoinedTeamsCaches(List<UUID> memberIds);
}
