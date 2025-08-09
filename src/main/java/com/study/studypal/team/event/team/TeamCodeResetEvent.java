package com.study.studypal.team.event.team;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TeamCodeResetEvent {
    private UUID teamId;
    private List<UUID> memberIds;
}
