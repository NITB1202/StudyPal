package com.study.studypal.session.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.session.dto.session.request.SaveSessionRequestDto;
import com.study.studypal.session.dto.session.response.SessionResponseDto;
import com.study.studypal.session.dto.session.response.SessionStatisticsResponseDto;
import com.study.studypal.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions")
public class SessionController {
  private final SessionService sessionService;

  @PostMapping
  @Operation(summary = "Save a study session.")
  @ApiResponse(responseCode = "200", description = "Save successfully.")
  @BadRequestApiResponse
  public ResponseEntity<SessionResponseDto> saveSession(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody SaveSessionRequestDto request) {
    return ResponseEntity.ok(sessionService.saveSession(userId, request));
  }

  @GetMapping("/statistics")
  @Operation(summary = "Get the study session's statistics.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<SessionStatisticsResponseDto> getSessionStatistics(
      @AuthenticationPrincipal UUID userId,
      @RequestParam LocalDateTime fromDate,
      @RequestParam LocalDateTime toDate) {
    return ResponseEntity.ok(sessionService.getSessionStatistics(userId, fromDate, toDate));
  }
}
