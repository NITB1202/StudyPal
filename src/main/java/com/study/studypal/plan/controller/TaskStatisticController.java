package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.statistic.request.GetTeamTaskDetailStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.request.SearchMemberTaskStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskDetailStatisticsResponseDto;
import com.study.studypal.plan.service.api.TaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskStatisticController {
  private final TaskStatisticService taskStatisticService;

  @GetMapping("/api/tasks/statistics")
  @Operation(summary = "Get user task detail statistics.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<TaskDetailStatisticsResponseDto> getTaskDetailStatistics(
      @AuthenticationPrincipal UUID userId,
      @RequestParam LocalDateTime fromDate,
      @RequestParam LocalDateTime toDate) {
    return ResponseEntity.ok(
        taskStatisticService.getTaskDetailStatistics(userId, fromDate, toDate));
  }

  @PostMapping("/api/teams/{teamId}/tasks/statistics")
  @Operation(summary = "Get team task detail statistics.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<TaskDetailStatisticsResponseDto> getTeamTaskDetailStatistics(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @Valid @RequestBody GetTeamTaskDetailStatisticsRequestDto request) {
    return ResponseEntity.ok(
        taskStatisticService.getTeamTaskDetailStatistics(userId, teamId, request));
  }

  @PostMapping("/api/teams/{teamId}/tasks/statistics/search")
  @Operation(summary = "Search team member task statistics.")
  @ApiResponse(responseCode = "200", description = "Search successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListTaskStatisticsResponseDto> searchTeamTaskStatistics(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @Valid @RequestBody SearchMemberTaskStatisticsRequestDto request) {
    return ResponseEntity.ok(
        taskStatisticService.searchMemberTaskStatistics(userId, teamId, request));
  }
}
