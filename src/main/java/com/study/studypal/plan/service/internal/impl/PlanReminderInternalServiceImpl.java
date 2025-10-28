package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.PlanReminder;
import com.study.studypal.plan.exception.PlanReminderErrorCode;
import com.study.studypal.plan.repository.PlanReminderRepository;
import com.study.studypal.plan.service.internal.PlanReminderInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanReminderInternalServiceImpl implements PlanReminderInternalService {
  private final PlanReminderRepository planReminderRepository;
  @PersistenceContext private final EntityManager entityManager;
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public void createRemindersForPersonalPlan(PlanInfo planInfo, List<LocalDateTime> reminders) {
    List<PlanReminder> savedReminders = new ArrayList<>();
    Set<LocalDateTime> savedTimes = new HashSet<>();

    for (LocalDateTime remindAt : reminders) {
      if (savedTimes.contains(remindAt)) {
        throw new BaseException(
            PlanReminderErrorCode.REMINDER_ALREADY_EXISTS, remindAt.format(formatter));
      } else {
        savedTimes.add(remindAt);
      }

      if (remindAt.isBefore(planInfo.getPlanStartDate())
          || remindAt.isAfter(planInfo.getPlanDueDate())) {
        throw new BaseException(PlanReminderErrorCode.INVALID_REMINDER, remindAt.format(formatter));
      }

      Plan plan = entityManager.getReference(Plan.class, planInfo.getPlanId());
      PlanReminder planReminder = PlanReminder.builder().plan(plan).remindAt(remindAt).build();

      savedReminders.add(planReminder);
    }

    planReminderRepository.saveAll(savedReminders);
  }

  @Override
  public List<LocalDateTime> getAll(UUID planId) {
    List<PlanReminder> reminders = planReminderRepository.findAllByPlanIdOrderByRemindAtAsc(planId);
    return reminders.stream().map(PlanReminder::getRemindAt).toList();
  }
}
