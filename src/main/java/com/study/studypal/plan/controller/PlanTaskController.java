package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.service.api.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanTaskController {
  private final TaskService taskService;

  @PostMapping("/{planId}/tasks")
  @Operation(summary = "Create a new task for plan.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<CreateTaskResponseDto> createTaskForPlan(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID planId,
      @Valid @RequestBody CreateTaskForPlanRequestDto request) {
    return ResponseEntity.ok(taskService.createTaskForPlan(userId, planId, request));
  }
}
