package com.study.studypal.notification.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.notification.dto.token.RegisterDeviceTokenRequestDto;
import com.study.studypal.notification.service.api.DeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/device-tokens")
public class DeviceTokenController {
  private final DeviceTokenService deviceTokenService;

  @PostMapping
  @Operation(summary = "Register a device token to receive real-time notifications.")
  @ApiResponse(responseCode = "200", description = "Register successfully.")
  public ResponseEntity<ActionResponseDto> registerDeviceToken(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody RegisterDeviceTokenRequestDto request) {
    return ResponseEntity.ok(deviceTokenService.registerDeviceToken(userId, request));
  }

  @DeleteMapping
  @Operation(summary = "Remove a device token, used when log out.")
  @ApiResponse(responseCode = "200", description = "Remove successfully.")
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> removeDeviceToken(
      @AuthenticationPrincipal UUID userId, @RequestParam String token) {
    return ResponseEntity.ok(deviceTokenService.removeDeviceToken(userId, token));
  }
}
