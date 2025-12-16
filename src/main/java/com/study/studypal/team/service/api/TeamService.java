package com.study.studypal.team.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamDashboardResponseDto;
import com.study.studypal.team.dto.team.response.TeamPreviewResponseDto;
import com.study.studypal.team.dto.team.response.TeamQRCodeResponseDto;
import com.study.studypal.team.dto.team.response.TeamResponseDto;
import com.study.studypal.team.enums.TeamFilter;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface TeamService {
  TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request);

  TeamDashboardResponseDto getTeamDashboard(UUID userId, UUID teamId);

  TeamQRCodeResponseDto getTeamQRCode(UUID userId, UUID teamId, int width, int height);

  TeamPreviewResponseDto getTeamPreview(String teamCode);

  ListTeamResponseDto searchTeamsByName(
      UUID userId, TeamFilter filter, String keyword, LocalDateTime cursor, int size);

  TeamResponseDto updateTeam(
      UUID userId, UUID teamId, UpdateTeamRequestDto request, MultipartFile file);

  ActionResponseDto resetTeamCode(UUID userId, UUID teamId);

  ActionResponseDto deleteTeam(UUID teamId, UUID userId);
}
