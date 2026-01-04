package com.study.studypal.team.event.team;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamUpdatedEvent {
  private UUID teamId;
  private String teamName;
  private UUID updatedBy;
  private boolean shouldEvictCache;
}

// Message: User01 updated the general information of Team01. -> Linked: teamId
