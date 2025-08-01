package com.study.studypal.team.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.team.dto.TeamUser.response.UserRoleInTeamResponseDto;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TeamMembershipService {
    ActionResponseDto joinTeam(UUID userId, UUID teamId);
    UserRoleInTeamResponseDto getUserRoleInTeam(UUID userId, UUID teamId);
    ListTeamMemberResponseDto getTeamMembers(UUID teamId, String cursor, int size);
    ListTeamMemberResponseDto searchTeamMembersByName(UUID userId, UUID teamId, String keyword, UUID cursor, int size);
    ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request);
    ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request);
    ActionResponseDto leaveTeam(UUID userId, UUID teamId);

    void createMembership(UUID teamId, UUID userId, TeamRole role);
    TeamUser getMemberShip(UUID teamId, UUID userId);
    LocalDateTime getUserJoinedTeamsListCursor(UUID userId, UUID lastTeamId, int listSize, int size);
    void validateUpdateTeamPermission(UUID userId, UUID teamId);
}
