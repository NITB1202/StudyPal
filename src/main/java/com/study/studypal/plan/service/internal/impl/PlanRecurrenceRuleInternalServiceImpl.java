package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.recurrence.request.CreatePlanRecurrenceRuleDto;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.TaskRecurrenceRule;
import com.study.studypal.plan.exception.TaskRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.TaskRecurrenceRuleRepository;
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
  private final TaskRecurrenceRuleRepository planRecurrenceRuleRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createPlanRecurrenceRule(TaskInfo planInfo, CreatePlanRecurrenceRuleDto ruleDto) {
    LocalDate recurrenceStartDate = planInfo.getStartDate().toLocalDate();
    LocalDate recurrenceEndDate = ruleDto.getRecurrenceEndDate();

    if (!recurrenceEndDate.isAfter(planInfo.getDueDate().toLocalDate())) {
      throw new BaseException(TaskRecurrenceRuleErrorCode.INVALID_END_DATE);
    }

    Plan plan = entityManager.getReference(Plan.class, planInfo.getId());
    String weekDays =
        ruleDto.getWeekDays().stream().map(DayOfWeek::name).collect(Collectors.joining(","));

    TaskRecurrenceRule rule =
        TaskRecurrenceRule.builder()
            .weekDays(weekDays)
            .recurrenceStartDate(recurrenceStartDate)
            .recurrenceEndDate(recurrenceEndDate)
            .build();

    planRecurrenceRuleRepository.save(rule);

    // Add a trigger to handle later
  }
}
