package com.study.studypal.team.service;

import java.util.UUID;

public interface TeamInternalService {
    UUID getTeamIdByTeamCode(String teamCode);
    void increaseMember(UUID teamId);
    void decreaseMember(UUID teamId);
}
