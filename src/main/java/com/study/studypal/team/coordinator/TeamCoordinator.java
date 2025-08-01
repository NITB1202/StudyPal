package com.study.studypal.team.coordinator;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.Team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.Team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.Team.response.TeamOverviewResponseDto;
import com.study.studypal.team.dto.Team.response.TeamResponseDto;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.service.TeamMembershipService;
import com.study.studypal.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamCoordinator {
    private final TeamService teamService;
    private final TeamMembershipService teamMembershipService;

    public TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request) {
        TeamResponseDto team = teamService.createTeam(userId, request);
        teamMembershipService.createMembership(team.getId(), userId, TeamRole.CREATOR);
        return team;
    }

    public TeamOverviewResponseDto getTeamOverview(UUID userId, UUID teamId) {
        TeamOverviewResponseDto team = teamService.getTeamOverview(userId, teamId);
        TeamUser membership = teamMembershipService.getMemberShip(teamId, userId);

        if(membership.getRole() == TeamRole.MEMBER) {
            team.setTeamCode(null);
        }

        return team;
    }

    public ListTeamResponseDto getUserJoinedTeams(UUID userId, LocalDateTime cursor, int size) {
        ListTeamResponseDto list = teamService.getUserJoinedTeams(userId, cursor, size);
        UUID lastTeamId = list.getTeams().get(list.getTeams().size() - 1).getId();
        TeamUser membership = teamMembershipService.getMemberShip(lastTeamId, userId);
        LocalDateTime nextCursor = !list.getTeams().isEmpty() && list.getTeams().size() == size ? membership.getJoinedAt() : null;
        list.setNextCursor(nextCursor);
        return list;
    }

    public ListTeamResponseDto searchUserJoinedTeamsByName(UUID userId, String keyword, LocalDateTime cursor, int size) {
        ListTeamResponseDto list = teamService.searchUserJoinedTeamsByName(userId, keyword, cursor, size);
        UUID lastTeamId = list.getTeams().get(list.getTeams().size() - 1).getId();
        TeamUser membership = teamMembershipService.getMemberShip(lastTeamId, userId);
        LocalDateTime nextCursor = !list.getTeams().isEmpty() && list.getTeams().size() == size ? membership.getJoinedAt() : null;
        list.setNextCursor(nextCursor);
        return list;
    }

    public TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request) {
        teamMembershipService.validateUpdateTeamPermission(userId, teamId);
        return teamService.updateTeam(userId, teamId, request);
    }

    public ActionResponseDto resetTeamCode(UUID userId, UUID teamId) {
        teamMembershipService.validateUpdateTeamPermission(userId, teamId);
        return teamService.resetTeamCode(userId, teamId);
    }

    public ActionResponseDto deleteTeam(UUID teamId, UUID userId) {
        teamMembershipService.validateUpdateTeamPermission(userId, teamId);
        return teamService.deleteTeam(userId, teamId);
    }

    public ActionResponseDto uploadTeamAvatar(UUID userId, UUID teamId, MultipartFile file) {
        teamMembershipService.validateUpdateTeamPermission(userId, teamId);
        return teamService.uploadTeamAvatar(userId, teamId, file);
    }
}
