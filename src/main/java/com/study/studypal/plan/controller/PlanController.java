package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.history.ListPlanHistoryResponseDto;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.request.UpdatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import com.study.studypal.plan.dto.plan.response.UpdatePlanResponseDto;
import com.study.studypal.plan.service.api.PlanHistoryService;
import com.study.studypal.plan.service.api.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlanController {
  private final PlanService planService;
  private final PlanHistoryService historyService;

  @PostMapping("/api/plans")
  @Operation(summary = "Create a new plan.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<CreatePlanResponseDto> createPlan(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CreatePlanRequestDto request) {
    return ResponseEntity.ok(planService.createPlan(userId, request));
  }

  @GetMapping("/api/plans/{planId}")
  @Operation(summary = "Get plan's details")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<PlanDetailResponseDto> getPlanDetail(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID planId) {
    return ResponseEntity.ok(planService.getPlanDetail(userId, planId));
  }

  @GetMapping("/api/teams/{teamId}/plans")
  @Operation(summary = "Get all team plans for a specific date.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<PlanSummaryResponseDto>> getPlansOnDate(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestParam LocalDate date) {
    return ResponseEntity.ok(planService.getPlansOnDate(userId, teamId, date));
  }

  @GetMapping("/api/teams/{teamId}/plans/dates")
  @Operation(summary = "Get dates with team plan due dates in a month.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<String>> getDatesWithTaskDueDateInMonth(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer year) {
    return ResponseEntity.ok(
        planService.getDatesWithPlanDueDatesInMonth(userId, teamId, month, year));
  }

  @GetMapping("/api/plans/{planId}/history")
  @Operation(summary = "Get plan history")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListPlanHistoryResponseDto> getPlanHistory(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID planId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(historyService.getPlanHistory(userId, planId, cursor, size));
  }

  @PatchMapping("/api/plans/{planId}")
  @Operation(summary = "Update a plan.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<UpdatePlanResponseDto> updatePlan(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID planId,
      @Valid @RequestBody UpdatePlanRequestDto request) {
    return ResponseEntity.ok(planService.updatePlan(userId, planId, request));
  }
}
