package com.study.studypal.team.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.team.dto.team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamDashboardResponseDto;
import com.study.studypal.team.dto.team.response.TeamPreviewResponseDto;
import com.study.studypal.team.dto.team.response.TeamResponseDto;
import com.study.studypal.team.service.api.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
  private final TeamService teamService;

  @PostMapping
  @Operation(summary = "Create a new team.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @BadRequestApiResponse
  public ResponseEntity<TeamResponseDto> createTeam(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateTeamRequestDto request) {
    return ResponseEntity.ok(teamService.createTeam(userId, request));
  }

  @GetMapping("/{teamId}")
  @Operation(summary = "Get team dashboard.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @NotFoundApiResponse
  public ResponseEntity<TeamDashboardResponseDto> getTeamDashboard(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(teamService.getTeamDashboard(userId, teamId));
  }

  @GetMapping("/code/{qrCode}")
  @Operation(summary = "Get team's preview by QR code.")
  @ApiResponse(responseCode = "200", description = "Get successfully")
  @NotFoundApiResponse
  public ResponseEntity<TeamPreviewResponseDto> getTeamPreview(@PathVariable String qrCode) {
    return ResponseEntity.ok(teamService.getTeamPreview(qrCode));
  }

  @GetMapping("/all")
  @Operation(summary = "Get part of the user's teams.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @NotFoundApiResponse
  public ResponseEntity<ListTeamResponseDto> getUserJoinedTeams(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive int size) {
    return ResponseEntity.ok(teamService.getUserJoinedTeams(userId, cursor, size));
  }

  @GetMapping("/search")
  @Operation(summary = "Search for user's teams by name.")
  @ApiResponse(responseCode = "200", description = "Search successfully.")
  @NotFoundApiResponse
  public ResponseEntity<ListTeamResponseDto> searchUserJoinedTeamsByName(
      @AuthenticationPrincipal UUID userId,
      @RequestParam String keyword,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Positive int size) {
    return ResponseEntity.ok(
        teamService.searchUserJoinedTeamsByName(userId, keyword, cursor, size));
  }

  @PatchMapping("/{teamId}")
  @Operation(summary = "Update team's profile.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<TeamResponseDto> updateTeam(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @Valid @RequestBody UpdateTeamRequestDto request) {
    return ResponseEntity.ok(teamService.updateTeam(userId, teamId, request));
  }

  @PatchMapping("/reset/{teamId}")
  @Operation(summary = "Reset team code.")
  @ApiResponse(responseCode = "200", description = "Reset successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> resetTeamCode(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(teamService.resetTeamCode(userId, teamId));
  }

  @DeleteMapping("/{teamId}")
  @Operation(summary = "Delete a team.")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> deleteTeam(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(teamService.deleteTeam(teamId, userId));
  }

  @PostMapping(value = "/avatar/{teamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload team's avatar.")
  @ApiResponse(responseCode = "200", description = "Upload successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> uploadTeamAvatar(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(teamService.uploadTeamAvatar(userId, teamId, file));
  }
}
