package com.study.studypal.plan.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.plan.dto.reminder.request.CreateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.response.TaskReminderResponseDto;
import com.study.studypal.plan.service.api.TaskReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/{taskId}/reminders")
public class TaskReminderController {
  private final TaskReminderService reminderService;

  @PostMapping
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

  @GetMapping
  @Operation(summary = "Get all task reminders.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<List<TaskReminderResponseDto>> getTaskReminders(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID taskId) {
    return ResponseEntity.ok(reminderService.getAll(userId, taskId));
  }
}
