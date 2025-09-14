package com.study.studypal.notification.controller;

import com.study.studypal.common.exception.ErrorResponse;
import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingsRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingsResponseDto;
import com.study.studypal.notification.service.api.TeamNotificationSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team-notification-settings")
public class TeamNotificationSettingsController {
  private final TeamNotificationSettingsService teamNotificationSettingsService;

  @GetMapping("/{teamId}")
  @Operation(summary = "Get notification settings of a user in a team.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @ApiResponse(
      responseCode = "404",
      description = "Not found.",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<TeamNotificationSettingsResponseDto> getTeamNotificationSettings(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(
        teamNotificationSettingsService.getTeamNotificationSettings(userId, teamId));
  }

  @PatchMapping("/{settingId}")
  @Operation(summary = "Update team notification settings.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized.",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Not found.",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  public ResponseEntity<TeamNotificationSettingsResponseDto> updateTeamNotificationSettings(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID settingId,
      @Valid @RequestBody UpdateTeamNotificationSettingsRequestDto request) {
    return ResponseEntity.ok(
        teamNotificationSettingsService.updateTeamNotificationSettings(userId, settingId, request));
  }
}
