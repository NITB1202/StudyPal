package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.plan.request.CreateTeamPlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.service.api.TeamPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/plans")
public class TeamPlanController {
  private final TeamPlanService teamPlanService;

  @PostMapping
  @Operation(summary = "Create a team plan.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  public ResponseEntity<CreatePlanResponseDto> createTeamPlan(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateTeamPlanRequestDto request) {
    return ResponseEntity.ok(teamPlanService.createTeamPlan(userId, request));
  }
}
