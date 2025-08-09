package com.study.studypal.team.event.team;


import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TeamDeletedEvent {
    private UUID teamId;
    private String teamName;
    private UUID deletedBy;
    private List<UUID> memberIds;
}

//Message: User01 has deleted the team 'TEAM01'.
