package com.study.studypal.team.event.team;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TeamUpdatedEvent {
    private UUID teamId;
    private String teamName;
    private UUID updatedBy;
    private List<UUID> memberIds;
    private boolean shouldEvictCache;
}

//Message: User01 updated the general information of team 'TEAM01'. -> Linked: teamId