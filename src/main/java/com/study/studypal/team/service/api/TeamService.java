package com.study.studypal.team.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamDashboardResponseDto;
import com.study.studypal.team.dto.team.response.TeamProfileResponseDto;
import com.study.studypal.team.dto.team.response.TeamResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface TeamService {
  TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request);

  TeamDashboardResponseDto getTeamDashboard(UUID userId, UUID teamId);

  TeamProfileResponseDto getTeamProfileByTeamCode(String teamCode);

  ListTeamResponseDto getUserJoinedTeams(UUID userId, LocalDateTime cursor, int size);

  ListTeamResponseDto searchUserJoinedTeamsByName(
      UUID userId, String keyword, LocalDateTime cursor, int size);

  TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request);

  ActionResponseDto resetTeamCode(UUID userId, UUID teamId);

  ActionResponseDto deleteTeam(UUID teamId, UUID userId);

  ActionResponseDto uploadTeamAvatar(UUID userId, UUID teamId, MultipartFile file);
}
