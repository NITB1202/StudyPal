package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Task;
import java.util.List;
import java.util.UUID;

public interface TaskInternalService {
  void createTasksForPlan(PlanInfo planInfo, List<CreateTaskForPlanRequestDto> tasks);

  Task getById(UUID id);

  List<TaskResponseDto> getAll(UUID planId);
}
