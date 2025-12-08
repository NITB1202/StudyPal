package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import java.util.UUID;

public interface PlanTaskService {
  CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request);

  UpdateTaskResponseDto updateTaskForPlan(
      UUID userId, UUID taskId, UpdateTaskForPlanRequestDto request);

  ActionResponseDto deleteTaskForPlan(UUID userId, UUID taskId);

  ActionResponseDto recoverTaskForPlan(UUID userId, UUID taskId);
}
