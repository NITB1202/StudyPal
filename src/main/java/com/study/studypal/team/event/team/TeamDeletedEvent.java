package com.study.studypal.team.event.team;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamDeletedEvent {
  private UUID teamId;
  private String teamName;
  private UUID deletedBy;
  private List<UUID> memberIds;
}

// Message: TEAM01 was deleted by User01.
