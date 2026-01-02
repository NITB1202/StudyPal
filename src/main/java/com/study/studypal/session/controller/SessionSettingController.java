package com.study.studypal.session.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.session.dto.setting.request.UpdateSessionSettingRequestDto;
import com.study.studypal.session.dto.setting.response.SessionSettingResponseDto;
import com.study.studypal.session.service.SessionSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions/settings")
public class SessionSettingController {
  private final SessionSettingService sessionSettingService;

  @GetMapping
  @Operation(summary = "Get user's session settings")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @NotFoundApiResponse
  public ResponseEntity<SessionSettingResponseDto> getSessionSetting(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(sessionSettingService.getSessionSetting(userId));
  }

  @PutMapping
  @Operation(summary = "Update user's session settings")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @BadRequestApiResponse
  @NotFoundApiResponse
  public ResponseEntity<SessionSettingResponseDto> updateSessionSetting(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody UpdateSessionSettingRequestDto request) {
    return ResponseEntity.ok(sessionSettingService.updateSessionSetting(userId, request));
  }
}
