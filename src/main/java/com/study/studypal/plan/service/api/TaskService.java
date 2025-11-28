package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
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

  UpdateTaskResponseDto updateTask(UUID userId, UUID taskId, UpdateTaskRequestDto request);

  ActionResponseDto deleteTask(UUID userId, UUID taskId);
}
