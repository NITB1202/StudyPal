package com.study.studypal.plan.controller;

import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.statistic.request.GetTaskDetailStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.request.SearchMemberTaskStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskDetailStatisticsResponseDto;
import com.study.studypal.plan.service.api.TaskStatisticService;
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
@RequestMapping("/api/teams/{teamId}/tasks/statistics")
public class TaskStatisticController {
  private final TaskStatisticService taskStatisticService;

  @PostMapping
  @Operation(summary = "Get team task statistics.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<TaskDetailStatisticsResponseDto> getTaskDetailStatistics(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @Valid @RequestBody GetTaskDetailStatisticsRequestDto request) {
    return ResponseEntity.ok(taskStatisticService.getTaskDetailStatistics(userId, teamId, request));
  }

  @PostMapping("/search")
  @Operation(summary = "Search team member task statistics.")
  @ApiResponse(responseCode = "200", description = "Search successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListTaskStatisticsResponseDto> searchTaskStatistics(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @Valid @RequestBody SearchMemberTaskStatisticsRequestDto request) {
    return ResponseEntity.ok(taskStatisticService.searchTaskStatistics(userId, teamId, request));
  }
}
