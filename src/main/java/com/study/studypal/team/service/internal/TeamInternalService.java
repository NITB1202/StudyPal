package com.study.studypal.team.service.internal;

import java.util.UUID;

public interface TeamInternalService {
  UUID getIdByTeamCode(String teamCode);

  void increaseMember(UUID teamId);

  void decreaseMember(UUID teamId);

  String getTeamName(UUID teamId);

  long countTeamsOwnerByUser(UUID userId);
}
