package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanInternalServiceImpl implements PlanInternalService {
  private final PlanRepository planRepository;
  private final TaskInternalService taskService;
  private final TaskNotificationService notificationService;

  @Override
  public UUID getTeamIdById(UUID id) {
    Plan plan =
        planRepository
            .findByIdWithTeam(id)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    return plan.getTeam().getId();
  }

  @Override
  public void updatePlanProgress(UUID id) {
    Plan plan =
        planRepository
            .findByIdWithTeam(id)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    int totalTasks = taskService.getTotalTasksCount(id);
    int completedTasks = taskService.getCompletedTasksCount(id);

    float progress = totalTasks != 0 ? (float) completedTasks / totalTasks : 0f;
    float roundedProgress = Math.round(progress * 100f) / 100f;

    plan.setProgress(roundedProgress);
    planRepository.save(plan);

    if (progress == 1) notificationService.publishPlanCompletedNotification(plan);
  }
}
