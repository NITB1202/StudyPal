package com.study.studypal.team.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.Team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.Team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.Team.response.TeamOverviewResponseDto;
import com.study.studypal.team.dto.Team.response.TeamProfileResponseDto;
import com.study.studypal.team.dto.Team.response.TeamResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TeamService {
    TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request);
    TeamOverviewResponseDto getTeamOverview(UUID userId, UUID teamId);
    TeamProfileResponseDto getTeamProfileByTeamCode(String teamCode);
    ListTeamResponseDto getUserJoinedTeams(UUID userId, LocalDateTime cursor, int size);
    ListTeamResponseDto searchUserJoinedTeamsByName(UUID userId, String keyword, LocalDateTime cursor, int size);
    TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request);
    ActionResponseDto resetTeamCode(UUID userId, UUID teamId);
    ActionResponseDto deleteTeam(UUID teamId, UUID userId);
    ActionResponseDto uploadTeamAvatar(UUID userId, UUID teamId, MultipartFile file);

    UUID getTeamIdByTeamCode(String teamCode);
    void increaseMember(UUID teamId);
    void decreaseMember(UUID teamId);
}