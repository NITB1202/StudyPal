package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.recurrence.request.CreatePlanRecurrenceRuleDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.PlanRecurrenceRule;
import com.study.studypal.plan.exception.PlanRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.PlanRecurrenceRuleRepository;
import com.study.studypal.plan.service.internal.PlanRecurrenceRuleInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanRecurrenceRuleInternalServiceImpl implements PlanRecurrenceRuleInternalService {
  private final PlanRecurrenceRuleRepository planRecurrenceRuleRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createPlanRecurrenceRule(PlanInfo planInfo, CreatePlanRecurrenceRuleDto ruleDto) {
    LocalDate recurrenceStartDate = planInfo.getPlanStartDate().toLocalDate();
    LocalDate recurrenceEndDate = ruleDto.getRecurrenceEndDate();

    if (!recurrenceEndDate.isAfter(planInfo.getPlanDueDate().toLocalDate())) {
      throw new BaseException(PlanRecurrenceRuleErrorCode.INVALID_END_DATE);
    }

    Plan plan = entityManager.getReference(Plan.class, planInfo.getPlanId());
    String weekDays =
        ruleDto.getWeekDays().stream().map(DayOfWeek::name).collect(Collectors.joining(","));

    PlanRecurrenceRule rule =
        PlanRecurrenceRule.builder()
            .plan(plan)
            .weekDays(weekDays)
            .recurrenceStartDate(recurrenceStartDate)
            .recurrenceEndDate(recurrenceEndDate)
            .isDeleted(false)
            .build();

    planRecurrenceRuleRepository.save(rule);

    // Add a trigger to handle later
  }
}
