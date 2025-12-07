package com.study.studypal.plan.controller;

import com.study.studypal.plan.dto.task.response.TaskDetailStatisticsResponseDto;
import com.study.studypal.plan.service.api.TaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/tasks/statistics")
public class TaskStatisticController {
  private final TaskStatisticService taskStatisticService;

  @GetMapping
  @Operation(summary = "Get team task statistics.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<TaskDetailStatisticsResponseDto> getTaskDetailStatistics(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestParam(required = false) UUID memberId) {
    return ResponseEntity.ok(
        taskStatisticService.getTaskDetailStatistics(userId, teamId, memberId));
  }
}
