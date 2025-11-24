package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskRecurrenceRule;
import com.study.studypal.plan.enums.RecurrenceType;
import com.study.studypal.plan.exception.TaskRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.TaskRecurrenceRuleRepository;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskRecurrenceRuleServiceImpl implements TaskRecurrenceRuleService {
  private final TaskRecurrenceRuleRepository ruleRepository;
  private final TaskInternalService taskService;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createRecurrenceRule(TaskInfo taskInfo, CreateTaskRecurrenceRuleRequestDto request) {
    if (request == null) return;

    LocalDate taskStartDate = taskInfo.getStartDate().toLocalDate();
    LocalDate taskDueDate = taskInfo.getDueDate().toLocalDate();

    if (!taskStartDate.equals(taskDueDate))
      throw new BaseException(TaskRecurrenceRuleErrorCode.RECURRING_TASK_DURATION_INVALID);

    if (!request.getRecurrenceEndDate().isAfter(taskStartDate))
      throw new BaseException(TaskRecurrenceRuleErrorCode.INVALID_END_DATE);

    if (request.getType().equals(RecurrenceType.WEEKLY)
        && CollectionUtils.isEmpty(request.getWeekDays()))
      throw new BaseException(TaskRecurrenceRuleErrorCode.INVALID_WEEKLY_RECURRENCE);

    Task task = entityManager.getReference(Task.class, taskInfo.getId());
    String weekDaysStr =
        request.getType().equals(RecurrenceType.WEEKLY)
            ? request.getWeekDays().stream().map(DayOfWeek::name).collect(Collectors.joining(","))
            : null;

    TaskRecurrenceRule rule =
        TaskRecurrenceRule.builder()
            .task(task)
            .recurrenceType(request.getType())
            .recurrenceStartDate(taskStartDate.plusDays(1))
            .recurrenceEndDate(request.getRecurrenceEndDate())
            .weekDays(weekDaysStr)
            .build();

    ruleRepository.save(rule);
    cloneTaskUtilRecurrenceEndDate(taskInfo, rule);
  }

  private void cloneTaskUtilRecurrenceEndDate(TaskInfo taskInfo, TaskRecurrenceRule rule) {
    List<LocalDate> recurrenceDates = new ArrayList<>();

    switch (rule.getRecurrenceType()) {
      case DAILY:
        {
          for (LocalDate i = rule.getRecurrenceStartDate();
              !i.isAfter(rule.getRecurrenceEndDate());
              i = i.plusDays(1)) {
            recurrenceDates.add(i);
          }

          break;
        }
      case WEEKLY:
        {
          List<DayOfWeek> weekDays =
              Arrays.stream(rule.getWeekDays().split(",")).map(DayOfWeek::valueOf).toList();

          for (LocalDate i = rule.getRecurrenceStartDate();
              !i.isAfter(rule.getRecurrenceEndDate());
              i = i.plusDays(1)) {
            if (weekDays.contains(i.getDayOfWeek())) {
              recurrenceDates.add(i);
            }
          }

          break;
        }
    }

    taskService.cloneTask(taskInfo.getId(), recurrenceDates);
  }
}
