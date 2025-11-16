package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.service.api.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {
  private final PlanService planService;

  @PostMapping
  @Operation(summary = "Create a personal plan.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @BadRequestApiResponse
  public ResponseEntity<CreatePlanResponseDto> createPersonalPlan(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody CreatePersonalPlanRequestDto request) {
    return ResponseEntity.ok(planService.createPersonalPlan(userId, request));
  }

  @GetMapping("/{planId}")
  @Operation(summary = "Get plan's details")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<PlanDetailResponseDto> getPlanDetail(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID planId) {
    return ResponseEntity.ok(planService.getPlanDetail(userId, planId));
  }
}
