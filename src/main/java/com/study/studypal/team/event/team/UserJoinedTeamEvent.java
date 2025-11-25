package com.study.studypal.team.event.team;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserJoinedTeamEvent {
  private UUID userId;
  private UUID teamId;
  private List<UUID> memberIds;
}

// Message: User01 joined Team01.
