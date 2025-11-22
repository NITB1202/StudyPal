package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.task.request.CreateTaskForPlanDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Task;
import java.util.List;
import java.util.UUID;

public interface TaskInternalService {
  void createTasksForPlan(UUID teamId, UUID planId, List<CreateTaskForPlanDto> tasks);

  Task getById(UUID id);

  List<TaskResponseDto> getAll(UUID planId);
}
