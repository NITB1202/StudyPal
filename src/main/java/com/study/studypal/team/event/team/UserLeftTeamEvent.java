package com.study.studypal.team.event.team;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLeftTeamEvent {
  private UUID userId;
  private UUID teamId;
}

// Message: User01 left Team01.
