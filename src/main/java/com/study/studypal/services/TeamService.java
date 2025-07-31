package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.Team.request.CreateTeamRequestDto;
import com.study.studypal.dtos.Team.request.UpdateTeamRequestDto;
import com.study.studypal.dtos.Team.response.ListTeamResponseDto;
import com.study.studypal.dtos.Team.response.TeamOverviewResponseDto;
import com.study.studypal.dtos.Team.response.TeamProfileResponseDto;
import com.study.studypal.dtos.Team.response.TeamResponseDto;
import org.springframework.http.codec.multipart.FilePart;

import java.util.UUID;

public interface TeamService {
    TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request);
    TeamOverviewResponseDto getTeamOverview(UUID teamId);
    TeamProfileResponseDto getTeamProfileByTeamCode(String teamCode);
    ListTeamResponseDto getUserJoinedTeams(UUID userId, String cursor, int size);
    ListTeamResponseDto searchUserJoinedTeamsByName(UUID userId, String keyword, String cursor, int size);
    TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request);
    ActionResponseDto resetTeamCode(UUID userId, UUID teamId);
    ActionResponseDto deleteTeam(UUID teamId, UUID userId);
    ActionResponseDto uploadTeamAvatar(UUID userId, UUID id, FilePart file);
}