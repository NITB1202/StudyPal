package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.PlanHistory;
import com.study.studypal.plan.repository.PlanHistoryRepository;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanHistoryInternalServiceImpl implements PlanHistoryInternalService {
  private final PlanHistoryRepository planHistoryRepository;
  private final UserInternalService userService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void logCreatePlan(UUID userId, UUID planId) {
    UserSummaryProfile user = userService.getUserSummaryProfile(userId);
    Plan plan = entityManager.getReference(Plan.class, planId);

    PlanHistory planHistory =
        PlanHistory.builder()
            .plan(plan)
            .imageUrl(user.getAvatarUrl())
            .message(user.getName() + " created the plan.")
            .timestamp(LocalDateTime.now())
            .build();

    planHistoryRepository.save(planHistory);
  }
}
