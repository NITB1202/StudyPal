package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.reminder.request.CreateReminderDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.PlanReminder;
import com.study.studypal.plan.exception.PlanReminderErrorCode;
import com.study.studypal.plan.repository.PlanReminderRepository;
import com.study.studypal.plan.service.internal.PlanReminderInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanReminderInternalServiceImpl implements PlanReminderInternalService {
  private final PlanReminderRepository planReminderRepository;
  private final Scheduler scheduler;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createRemindersForPersonalPlan(
      PlanInfo planInfo, List<CreateReminderDto> reminderDtoList) {
    List<PlanReminder> planReminderList = new ArrayList<>();

    for (CreateReminderDto reminderDto : reminderDtoList) {
      validateReminderTime(planInfo, reminderDto.getRemindAt());

      Plan plan = entityManager.getReference(Plan.class, planInfo.getPlanId());
      PlanReminder planReminder =
          PlanReminder.builder().plan(plan).remindAt(reminderDto.getRemindAt()).build();

      planReminderList.add(planReminder);
    }

    planReminderRepository.saveAll(planReminderList);
  }

  private void validateReminderTime(PlanInfo planInfo, LocalDateTime reminder) {
    if (reminder.isBefore(planInfo.getPlanStartDate())
        || reminder.isAfter(planInfo.getPlanDueDate())) {
      throw new BaseException(PlanReminderErrorCode.INVALID_REMINDER);
    }
  }
}
