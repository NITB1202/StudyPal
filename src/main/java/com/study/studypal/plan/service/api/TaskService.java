package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import java.util.UUID;

public interface TaskService {
  CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request);

  CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request);

  TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId);
}
