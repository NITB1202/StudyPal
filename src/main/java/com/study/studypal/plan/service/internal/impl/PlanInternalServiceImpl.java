package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import java.util.Set;
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
            .findById(id)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    int totalTasks = taskService.getTotalTasksCount(id);
    int completedTasks = taskService.getCompletedTasksCount(id);

    if (totalTasks == 0) {
      plan.setIsDeleted(true);
      planRepository.save(plan);
      return;
    }

    float progress = (float) completedTasks / totalTasks;
    float roundedProgress = Math.round(progress * 100f) / 100f;

    plan.setProgress(roundedProgress);
    planRepository.save(plan);

    if (roundedProgress >= 1.0f) {
      notificationService.publishPlanCompletedNotification(plan);
    }
  }

  @Override
  public Set<UUID> getPlanRelatedMemberIds(UUID planId) {
    Plan plan =
        planRepository
            .findById(planId)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    Set<UUID> taskAssigneeIds = taskService.getDistinctAssigneeIdsByPlanId(planId);
    taskAssigneeIds.add(plan.getCreator().getId());

    return taskAssigneeIds;
  }
}
