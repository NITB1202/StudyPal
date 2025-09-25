package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPersonalPlanDto;
import java.util.List;
import java.util.UUID;

public interface TaskInternalService {
  void createTasksForPersonalPlan(
      UUID userId, PlanInfo planInfo, List<CreateTaskForPersonalPlanDto> tasks);
}
