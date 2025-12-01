package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.PlanHistory;
import com.study.studypal.plan.repository.PlanHistoryRepository;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanHistoryInternalServiceImpl implements PlanHistoryInternalService {
  private final PlanHistoryRepository planHistoryRepository;
  private final UserInternalService userService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void logCreatePlan(UUID userId, UUID planId) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    String message = String.format("%s created the plan.", user.getName());
    logMessage(planId, message, user.getAvatarUrl());
  }

  @Override
  public void logAssignTask(UUID assignerId, UUID assigneeId, UUID planId, String taskCode) {
    UserSummaryProfile assigner = userService.getUserSummaryProfile(assignerId);
    UserSummaryProfile assignee = userService.getUserSummaryProfile(assigneeId);
    String message =
        String.format(
            "%s assigned task [%s] to %s.", assigner.getName(), taskCode, assignee.getName());
    logMessage(planId, message, assigner.getAvatarUrl());
  }

  @Override
  public void logUpdateTask(UUID userId, UUID planId, String taskCode) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    String message = String.format("%s updated task [%s].", user.getName(), taskCode);
    logMessage(planId, message, user.getAvatarUrl());
  }

  @Override
  public void logCompleteTask(UUID userId, UUID planId, String taskCode) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    String message = String.format("%s completed task [%s].", user.getName(), taskCode);
    logMessage(planId, message, user.getAvatarUrl());
  }

  @Override
  public void logDeleteTask(UUID userId, UUID planId, String taskCode) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    String message = String.format("%s deleted task [%s].", user.getName(), taskCode);
    logMessage(planId, message, user.getAvatarUrl());
  }

  @Override
  public void logDeletePlan(UUID userId, UUID planId) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    String message = String.format("%s deleted the plan.", user.getName());
    logMessage(planId, message, user.getAvatarUrl());
  }

  private void logMessage(UUID planId, String message, String imageUrl) {
    Plan plan = entityManager.getReference(Plan.class, planId);

    PlanHistory planHistory =
        PlanHistory.builder()
            .plan(plan)
            .imageUrl(imageUrl)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();

    planHistoryRepository.save(planHistory);
  }
}
