package com.study.studypal.plan.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.service.api.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;

  @PostMapping
  @Operation(summary = "Create a new task.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<CreateTaskResponseDto> createTask(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateTaskRequestDto request) {
    return ResponseEntity.ok(taskService.createTask(userId, request));
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get task's details")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<TaskDetailResponseDto> getTaskDetail(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(taskService.getTaskDetail(userId, taskId));
  }

  @GetMapping
  @Operation(summary = "Get assigned tasks for a specific date.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<TaskSummaryResponseDto>> getAssignedTasksOnDate(
      @AuthenticationPrincipal UUID userId, @RequestParam LocalDate date) {
    return ResponseEntity.ok(taskService.getAssignedTasksOnDate(userId, date));
  }

  @GetMapping("/dates")
  @Operation(summary = "Get dates with assigned task due dates in a month.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<String>> getDatesWithTaskDueDateInMonth(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer year) {
    return ResponseEntity.ok(taskService.getDatesWithTaskDueDateInMonth(userId, month, year));
  }

  @PatchMapping("/{taskId}")
  @Operation(summary = "Update a personal task.")
  @ApiResponse(responseCode = "200", description = "Update successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<UpdateTaskResponseDto> updateTask(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID taskId,
      @Valid @RequestBody UpdateTaskRequestDto request) {
    return ResponseEntity.ok(taskService.updateTask(userId, taskId, request));
  }

  @DeleteMapping("/{taskId}")
  @Operation(summary = "Delete a personal task")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> deleteTask(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(taskService.deleteTask(userId, taskId));
  }
}
