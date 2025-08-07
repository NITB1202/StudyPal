package com.study.studypal.team.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.TeamUser.response.ListTeamMemberResponseDto;

import java.util.UUID;

public interface TeamMembershipService {
    ActionResponseDto joinTeam(UUID userId, String teamCode);
    ListTeamMemberResponseDto getTeamMembers(UUID userId, UUID teamId, String cursor, int size);
    ListTeamMemberResponseDto searchTeamMembersByName(UUID userId, UUID teamId, String keyword, UUID cursor, int size);
    ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request);
    ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request);
    ActionResponseDto leaveTeam(UUID userId, UUID teamId);
}
