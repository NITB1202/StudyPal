package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.task.request.CreateTaskForPlanDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import java.util.List;
import java.util.UUID;

public interface TaskInternalService {
  void createTasksForPlan(UUID teamId, UUID planId, List<CreateTaskForPlanDto> tasks);

  List<TaskResponseDto> getAll(UUID planId);
}
