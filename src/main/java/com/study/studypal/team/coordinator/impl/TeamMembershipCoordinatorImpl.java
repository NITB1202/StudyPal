package com.study.studypal.team.coordinator.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.coordinator.TeamMembershipCoordinator;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.team.dto.TeamUser.response.UserRoleInTeamResponseDto;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.team.service.api.TeamMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamMembershipCoordinatorImpl implements TeamMembershipCoordinator {
    private final TeamInternalService teamService;
    private final TeamMembershipService teamMembershipService;

    @Override
    public ActionResponseDto joinTeam(UUID userId, String teamCode) {
        UUID teamId = teamService.getTeamIdByTeamCode(teamCode);
        ActionResponseDto response = teamMembershipService.joinTeam(userId, teamId);
        teamService.increaseMember(teamId);
        return response;
    }

    @Override
    public UserRoleInTeamResponseDto getUserRoleInTeam(UUID userId, UUID teamId) {
        return teamMembershipService.getUserRoleInTeam(userId, teamId);
    }

    @Override
    public ListTeamMemberResponseDto getTeamMembers(UUID teamId, String cursor, int size) {
        return teamMembershipService.getTeamMembers(teamId, cursor, size);
    }

    @Override
    public ListTeamMemberResponseDto searchTeamMembersByName(UUID userId, UUID teamId, String keyword, UUID cursor, int size) {
        return teamMembershipService.searchTeamMembersByName(userId, teamId, keyword, cursor, size);
    }

    @Override
    public ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request) {
        return teamMembershipService.updateTeamMemberRole(userId, request);
    }

    @Override
    public ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request) {
        ActionResponseDto response = teamMembershipService.removeTeamMember(userId, request);
        teamService.decreaseMember(request.getTeamId());
        return response;
    }

    @Override
    public ActionResponseDto leaveTeam(UUID userId, UUID teamId) {
        ActionResponseDto response = teamMembershipService.leaveTeam(userId, teamId);
        teamService.decreaseMember(teamId);
        return response;
    }
}
