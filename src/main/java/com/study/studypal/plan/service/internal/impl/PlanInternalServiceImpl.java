package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanInternalServiceImpl implements PlanInternalService {
  private final PlanRepository planRepository;
  private final TaskInternalService taskService;

  @Override
  public UUID getTeamIdById(UUID id) {
    Plan plan =
        planRepository
            .findByIdWithTeam(id)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    return plan.getTeam().getId();
  }

  @Override
  public void syncPlanFromTasks(Plan plan) {
    List<Task> tasks = taskService.getAll(plan.getId());

    int totalTasks = tasks.size();
    int completedTasks = 0;

    LocalDateTime startDate = LocalDateTime.MAX;
    LocalDateTime dueDate = LocalDateTime.MIN;

    for (Task task : tasks) {
      if (task.getStartDate().isBefore(startDate)) {
        startDate = task.getStartDate();
      }

      if (task.getDueDate().isAfter(dueDate)) {
        dueDate = task.getDueDate();
      }
    }

    float progress = totalTasks != 0 ? (float) completedTasks / totalTasks : 0f;
    float roundedProgress = Math.round(progress * 100f) / 100f;

    plan.setProgress(roundedProgress);
    plan.setStartDate(startDate);
    plan.setDueDate(dueDate);

    planRepository.save(plan);
  }

  @Override
  public void softDeletePlan(Plan plan) {
    plan.setIsDeleted(true);
    planRepository.save(plan);
  }

  @Override
  public void recoverPlan(Plan plan) {
    plan.setIsDeleted(false);
    planRepository.save(plan);
  }

  @Override
  public void purgeEmptySoftDeletedPlans() {
    planRepository.hardDeleteDeletedPlansWithoutTasks();
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
