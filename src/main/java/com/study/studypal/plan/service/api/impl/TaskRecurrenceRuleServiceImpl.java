package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.recurrence.request.UpdateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskRecurrenceRule;
import com.study.studypal.plan.enums.RecurrenceType;
import com.study.studypal.plan.exception.TaskRecurrenceRuleErrorCode;
import com.study.studypal.plan.repository.TaskRecurrenceRuleRepository;
import com.study.studypal.plan.service.api.TaskRecurrenceRuleService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskValidationService;
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
  private final TaskValidationService taskValidationService;
  private final ModelMapper modelMapper;

  @Override
  public ActionResponseDto updateRecurrenceRule(
      UUID userId, UUID taskId, UpdateTaskRecurrenceRuleRequestDto request) {
    Task task = taskService.getById(taskId);

    taskValidationService.validatePersonalTask(task);
    taskValidationService.validateUpdateTaskPermission(userId, task);

    Optional<TaskRecurrenceRule> rule = getRecurrenceRule(task);
    TaskRecurrenceRule existingRule = rule.orElse(null);

    RecurrenceType currentType =
        rule.isEmpty() ? RecurrenceType.NONE : rule.get().getRecurrenceType();
    RecurrenceType changeType = request.getType();

    ActionResponseDto successResponse =
        ActionResponseDto.builder().success(true).message("Update successfully.").build();

    if (currentType.equals(RecurrenceType.NONE) && changeType.equals(RecurrenceType.NONE)) {
      return successResponse;
    }

    if (!currentType.equals(RecurrenceType.NONE) && changeType.equals(RecurrenceType.NONE)) {
      deleteActiveClonedTasksExcludeCurrent(task);
      taskService.detachFromParent(task);
      deleteRecurrenceRule(existingRule);

      return successResponse;
    }

    validateRecurrenceRule(request, task);
    deleteActiveClonedTasksExcludeCurrent(task);
    taskService.detachFromParent(task);

    TaskRecurrenceRule updatedRule = updateRecurrenceRule(request, task, existingRule);
    cloneTaskUtilRecurrenceEndDate(taskId, updatedRule);

    return successResponse;
  }

  @Override
  public TaskRecurrenceRuleResponseDto getRecurrenceRule(UUID userId, UUID taskId) {
    Task task = taskService.getById(taskId);

    taskValidationService.validatePersonalTask(task);
    taskValidationService.validateViewTaskPermission(userId, task);

    Optional<TaskRecurrenceRule> rule = getRecurrenceRule(task);

    if (rule.isEmpty()) {
      return TaskRecurrenceRuleResponseDto.builder().recurrenceType(RecurrenceType.NONE).build();
    }

    TaskRecurrenceRuleResponseDto response =
        modelMapper.map(rule.get(), TaskRecurrenceRuleResponseDto.class);

    List<DayOfWeek> weekDays =
        StringUtils.isNotBlank(rule.get().getWeekDays())
            ? Arrays.stream(rule.get().getWeekDays().split(",")).map(DayOfWeek::valueOf).toList()
            : null;

    response.setWeekDays(weekDays);
    return response;
  }

  private Optional<TaskRecurrenceRule> getRecurrenceRule(Task task) {
    Task rootTask = task.getParentTask();
    UUID rootTaskId = rootTask != null ? rootTask.getId() : task.getId();
    return ruleRepository.findByTaskId(rootTaskId);
  }

  private void validateRecurrenceRule(UpdateTaskRecurrenceRuleRequestDto request, Task task) {
    LocalDate taskStartDate = task.getStartDate().toLocalDate();
    LocalDate taskDueDate = task.getDueDate().toLocalDate();

    LocalDate startDate =
        Optional.ofNullable(request.getRecurrenceStartDate()).orElse(taskStartDate.plusDays(1));
    LocalDate endDate = request.getRecurrenceEndDate();

    if (!taskStartDate.equals(taskDueDate)) {
      throw new BaseException(TaskRecurrenceRuleErrorCode.RECURRING_TASK_DURATION_INVALID);
    }

    if (endDate == null) {
      throw new BaseException(CommonErrorCode.FIELD_NULL, "End date");
    }

    if (startDate.isAfter(endDate)) {
      throw new BaseException(CommonErrorCode.INVALID_TIME_RANGE);
    }

    if (!startDate.isAfter(taskDueDate)) {
      throw new BaseException(TaskRecurrenceRuleErrorCode.START_DATE_AFTER_DUE_DATE);
    }

    if (request.getType().equals(RecurrenceType.WEEKLY)
        && CollectionUtils.isEmpty(request.getWeekDays())) {
      throw new BaseException(TaskRecurrenceRuleErrorCode.INVALID_WEEKLY_RECURRENCE);
    }
  }

  private TaskRecurrenceRule updateRecurrenceRule(
      UpdateTaskRecurrenceRuleRequestDto request, Task task, TaskRecurrenceRule rule) {
    LocalDate startDate =
        Optional.ofNullable(request.getRecurrenceStartDate())
            .orElse(task.getStartDate().toLocalDate().plusDays(1));

    LocalDate endDate = request.getRecurrenceEndDate();

    String weekDaysStr =
        request.getType().equals(RecurrenceType.WEEKLY)
            ? request.getWeekDays().stream().map(DayOfWeek::name).collect(Collectors.joining(","))
            : null;

    rule = Optional.ofNullable(rule).orElse(new TaskRecurrenceRule());

    rule.setTask(task);
    rule.setRecurrenceType(request.getType());
    rule.setRecurrenceStartDate(startDate);
    rule.setRecurrenceEndDate(endDate);
    rule.setWeekDays(weekDaysStr);

    return ruleRepository.save(rule);
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
      case NONE:
        break;
    }

    taskService.cloneTask(taskId, recurrenceDates);
  }

  private void deleteRecurrenceRule(TaskRecurrenceRule rule) {
    ruleRepository.delete(rule);
  }

  private void deleteActiveClonedTasksExcludeCurrent(Task task) {
    List<Task> tasks = taskService.getAllActiveClonedTasksIncludingOriginal(task);
    tasks.remove(task);
    taskService.hardDeleteTasks(tasks);
  }
}
