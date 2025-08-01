package com.study.studypal.team.coordinator;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.service.TeamMembershipService;
import com.study.studypal.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamMembershipCoordinator {
    private final TeamService teamService;
    private final TeamMembershipService teamMembershipService;

    public ActionResponseDto joinTeam(UUID userId, String teamCode) {
        UUID teamId = teamService.getTeamIdByTeamCode(teamCode);
        ActionResponseDto response = teamMembershipService.joinTeam(userId, teamId);
        teamService.increaseMember(teamId);
        return response;
    }

    public ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request) {
        ActionResponseDto response = teamMembershipService.removeTeamMember(userId, request);
        teamService.decreaseMember(request.getTeamId());
        return response;
    }

    public ActionResponseDto leaveTeam(UUID userId, UUID teamId) {
        ActionResponseDto response = teamMembershipService.leaveTeam(userId, teamId);
        teamService.decreaseMember(teamId);
        return response;
    }
}
