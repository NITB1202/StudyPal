package com.study.studypal.plan.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleService;
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
@RequestMapping("/api/tasks/{taskId}/recurrence-rules")
public class TaskRecurrenceRuleController {
  private final TaskRecurrenceRuleService ruleService;

  @PostMapping
  @Operation(summary = "Create a task recurrence rule (personal task only).")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<ActionResponseDto> createRecurrenceRule(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID taskId,
      @Valid @RequestBody CreateTaskRecurrenceRuleRequestDto request) {
    return ResponseEntity.ok(ruleService.createRecurrenceRule(userId, taskId, request));
  }

  @GetMapping
  @Operation(summary = "Get task's recurrence rule.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<TaskRecurrenceRuleResponseDto> getRecurrenceRule(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(ruleService.getRecurrenceRule(userId, taskId));
  }
}
