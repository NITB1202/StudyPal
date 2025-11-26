package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskRecurrenceRule;
import com.study.studypal.plan.enums.RecurrenceType;
import com.study.studypal.plan.exception.TaskRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.TaskRecurrenceRuleRepository;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleService;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskRecurrenceRuleServiceImpl implements TaskRecurrenceRuleService {
  private final TaskRecurrenceRuleRepository ruleRepository;
  private final TaskInternalService taskService;
  private final ModelMapper modelMapper;

  @Override
  public ActionResponseDto createRecurrenceRule(
      UUID userId, UUID taskId, CreateTaskRecurrenceRuleRequestDto request) {
    if (request.getType().equals(RecurrenceType.NONE))
      return ActionResponseDto.builder()
          .success(false)
          .message("Recurrence rule can not be created.")
          .build();

    Task task = taskService.getById(taskId);

    taskService.validatePersonalTask(task);
    taskService.validateTaskOwnership(userId, task);

    LocalDate taskStartDate = task.getStartDate().toLocalDate();
    LocalDate taskDueDate = task.getDueDate().toLocalDate();

    LocalDate endDate = request.getRecurrenceEndDate();
    LocalDate startDate =
        request.getRecurrenceStartDate() != null
            ? request.getRecurrenceStartDate()
            : taskStartDate.plusDays(1);

    if (!taskStartDate.equals(taskDueDate))
      throw new BaseException(TaskRecurrenceRuleErrorCode.RECURRING_TASK_DURATION_INVALID);

    if (startDate.isAfter(endDate))
      throw new BaseException(TaskRecurrenceRuleErrorCode.START_DATE_AFTER_END_DATE);

    if (!startDate.isAfter(taskDueDate))
      throw new BaseException(TaskRecurrenceRuleErrorCode.START_DATE_AFTER_DUE_DATE);

    if (request.getType().equals(RecurrenceType.WEEKLY)
        && CollectionUtils.isEmpty(request.getWeekDays()))
      throw new BaseException(TaskRecurrenceRuleErrorCode.INVALID_WEEKLY_RECURRENCE);

    String weekDaysStr =
        request.getType().equals(RecurrenceType.WEEKLY)
            ? request.getWeekDays().stream().map(DayOfWeek::name).collect(Collectors.joining(","))
            : null;

    TaskRecurrenceRule rule =
        TaskRecurrenceRule.builder()
            .task(task)
            .recurrenceType(request.getType())
            .recurrenceStartDate(startDate)
            .recurrenceEndDate(endDate)
            .weekDays(weekDaysStr)
            .build();

    ruleRepository.save(rule);
    cloneTaskUtilRecurrenceEndDate(taskId, rule);

    return ActionResponseDto.builder().success(true).message("Create successfully.").build();
  }

  @Override
  public TaskRecurrenceRuleResponseDto getRecurrenceRule(UUID userId, UUID taskId) {
    Task task = taskService.getById(taskId);

    taskService.validatePersonalTask(task);
    taskService.validateViewTaskPermission(userId, task);

    Optional<TaskRecurrenceRule> rule = ruleRepository.findByTaskId(taskId);

    if (rule.isEmpty())
      return TaskRecurrenceRuleResponseDto.builder().type(RecurrenceType.NONE).build();

    TaskRecurrenceRuleResponseDto response =
        modelMapper.map(rule.get(), TaskRecurrenceRuleResponseDto.class);

    List<DayOfWeek> weekDays =
        StringUtils.isNotBlank(rule.get().getWeekDays())
            ? Arrays.stream(rule.get().getWeekDays().split(",")).map(DayOfWeek::valueOf).toList()
            : null;

    response.setWeekDays(weekDays);
    return response;
  }

  private void cloneTaskUtilRecurrenceEndDate(UUID taskId, TaskRecurrenceRule rule) {
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

    taskService.cloneTask(taskId, recurrenceDates);
  }
}
