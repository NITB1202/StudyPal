package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.dtos.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.dtos.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.dtos.TeamUser.response.UserRoleInTeamResponseDto;

import java.util.UUID;

public interface TeamMembershipService {
    ActionResponseDto joinTeam(UUID userId, String teamCode);
    UserRoleInTeamResponseDto getUserRoleInTeam(UUID userId, UUID teamId);
    ListTeamMemberResponseDto getTeamMembers(UUID teamId, String cursor, int size);
    ListTeamMemberResponseDto searchTeamMembersByName(UUID userId, UUID teamId, String keyword, UUID cursor, int size);
    ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request);
    ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request);
    ActionResponseDto leaveTeam(UUID userId, UUID teamId);
}
