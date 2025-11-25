package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskService {
  CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request);

  CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request);

  TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId);

  List<TaskSummaryResponseDto> getAssignedTasksOnDate(UUID userId, LocalDate date);

  List<String> getDatesWithDeadlineInMonth(UUID userId, Integer month, Integer year);
}
