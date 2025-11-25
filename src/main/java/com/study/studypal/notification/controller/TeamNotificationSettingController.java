package com.study.studypal.notification.controller;

import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.notification.dto.setting.request.UpdateTeamNotificationSettingRequestDto;
import com.study.studypal.notification.dto.setting.response.TeamNotificationSettingResponseDto;
import com.study.studypal.notification.service.api.TeamNotificationSettingService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamNotificationSettingController {
  private final TeamNotificationSettingService teamNotificationSettingsService;

  @GetMapping("/api/teams/{teamId}/notification-settings")
  @Operation(summary = "Get notification setting of a user in a team.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @NotFoundApiResponse
  public ResponseEntity<TeamNotificationSettingResponseDto> getTeamNotificationSetting(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID teamId) {
    return ResponseEntity.ok(
        teamNotificationSettingsService.getTeamNotificationSetting(userId, teamId));
  }

  @PatchMapping("/api/notification-settings/{settingId}")
  @Operation(summary = "Update team notification setting.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<TeamNotificationSettingResponseDto> updateTeamNotificationSetting(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID settingId,
      @Valid @RequestBody UpdateTeamNotificationSettingRequestDto request) {
    return ResponseEntity.ok(
        teamNotificationSettingsService.updateTeamNotificationSetting(userId, settingId, request));
  }
}
