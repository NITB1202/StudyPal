package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.enums.ApplyScope;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {
  CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request);

  CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request);

  TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId);

  List<TaskSummaryResponseDto> getAssignedTasksOnDate(UUID userId, LocalDate date);

  List<String> getDatesWithTaskDueDateInMonth(UUID userId, Integer month, Integer year);

  UpdateTaskResponseDto updateTask(
      UUID userId, UUID taskId, ApplyScope applyScope, UpdateTaskRequestDto request);

  UpdateTaskResponseDto updateTaskForPlan(
      UUID userId, UUID taskId, UpdateTaskForPlanRequestDto request);

  ActionResponseDto markTaskAsCompleted(UUID userId, UUID taskId);

  ActionResponseDto deleteTask(UUID userId, UUID taskId, ApplyScope applyScope);

  ActionResponseDto deleteTaskForPlan(UUID userId, UUID taskId);
}
