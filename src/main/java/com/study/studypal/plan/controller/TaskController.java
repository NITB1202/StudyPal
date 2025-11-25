package com.study.studypal.plan.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import com.study.studypal.plan.dto.reminder.request.CreateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.response.TaskReminderResponseDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.service.api.TaskReminderService;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Valid
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;
  private final TaskReminderService reminderService;
  private final TaskRecurrenceRuleService ruleService;

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

  @PostMapping("/{taskId}/reminder")
  @Operation(summary = "Create a task reminder.")
  @ApiResponse(responseCode = "200", description = "Create successfully.")
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  public ResponseEntity<ActionResponseDto> createTaskReminder(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID taskId,
      @Valid @RequestBody CreateTaskReminderRequestDto request) {
    return ResponseEntity.ok(reminderService.createReminder(userId, taskId, request));
  }

  @GetMapping("/{taskId}/reminders")
  @Operation(summary = "Get all task reminders.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<List<TaskReminderResponseDto>> getTaskReminders(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(reminderService.getAll(userId, taskId));
  }

  @PostMapping("/{taskId}/recurrence-rule")
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

  @GetMapping("/{taskId}/recurrence-rule")
  @Operation(summary = "Get task's recurrence rule.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<TaskRecurrenceRuleResponseDto> getRecurrenceRule(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(ruleService.getRecurrenceRule(userId, taskId));
  }

  @GetMapping
  @Operation(summary = "Get assigned tasks for a specific date.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<TaskSummaryResponseDto>> getAssignedTasksOnDate(
      @AuthenticationPrincipal UUID userId, @RequestParam LocalDate date) {
    return ResponseEntity.ok(taskService.getAssignedTasksOnDate(userId, date));
  }

  @GetMapping("/dates")
  @Operation(summary = "Get dates with assigned task deadlines in a month.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<List<String>> getDatesWithDeadlineInMonth(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer year) {
    return ResponseEntity.ok(taskService.getDatesWithDeadlineInMonth(userId, month, year));
  }
}
