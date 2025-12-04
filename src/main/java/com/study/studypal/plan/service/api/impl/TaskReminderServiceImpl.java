package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.plan.constant.PlanConstant.JSON_DATETIME_FORMATTER;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.reminder.request.CreateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.request.UpdateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.response.TaskReminderResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskReminder;
import com.study.studypal.plan.exception.TaskReminderErrorCode;
import com.study.studypal.plan.repository.TaskReminderRepository;
import com.study.studypal.plan.service.api.TaskReminderService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskReminderServiceImpl implements TaskReminderService {
  private final TaskReminderRepository taskReminderRepository;
  private final TaskInternalService taskService;
  private final TaskReminderInternalService internalService;
  private final ModelMapper modelMapper;

  @Override
  public ActionResponseDto createReminder(
      UUID userId, UUID taskId, CreateTaskReminderRequestDto request) {
    Task task = taskService.getById(taskId);
    taskService.validateTaskOwnership(userId, task);

    LocalDateTime remindAt = request.getRemindAt();

    validateReminder(task, remindAt);
    internalService.scheduleReminder(remindAt, task);

    return ActionResponseDto.builder().success(true).message("Create successfully.").build();
  }

  @Override
  public List<TaskReminderResponseDto> getAll(UUID userId, UUID taskId) {
    Task task = taskService.getById(taskId);
    taskService.validateViewTaskPermission(userId, task);

    List<TaskReminder> reminders = taskReminderRepository.findAllByTaskIdOrderByRemindAtAsc(taskId);
    List<TaskReminder> filteredReminders =
        reminders.stream().filter(r -> !r.getRemindAt().equals(task.getDueDate())).toList();

    return modelMapper.map(
        filteredReminders, new TypeToken<List<TaskReminderResponseDto>>() {}.getType());
  }

  @Override
  public ActionResponseDto updateReminder(
      UUID userId, UUID reminderId, UpdateTaskReminderRequestDto request) {
    TaskReminder reminder =
        taskReminderRepository
            .findById(reminderId)
            .orElseThrow(() -> new BaseException(TaskReminderErrorCode.REMINDER_NOT_FOUND));

    Task task = reminder.getTask();
    taskService.validateTaskOwnership(userId, task);

    LocalDateTime remindAt = request.getRemindAt();

    if (!reminder.getRemindAt().equals(remindAt)) {
      validateReminder(task, remindAt);

      reminder.setRemindAt(remindAt);
      taskReminderRepository.save(reminder);

      internalService.rescheduleReminder(reminder);
    }

    return ActionResponseDto.builder().success(true).message("Update successfully.").build();
  }

  @Override
  public ActionResponseDto deleteReminder(UUID userId, UUID reminderId) {
    TaskReminder reminder =
        taskReminderRepository
            .findById(reminderId)
            .orElseThrow(() -> new BaseException(TaskReminderErrorCode.REMINDER_NOT_FOUND));

    Task task = reminder.getTask();
    taskService.validateTaskOwnership(userId, task);

    internalService.cancelReminder(reminder.getId());
    taskReminderRepository.delete(reminder);

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  private void validateReminder(Task task, LocalDateTime remindAt) {
    if (taskReminderRepository.existsByTaskIdAndRemindAt(task.getId(), remindAt)) {
      throw new BaseException(
          TaskReminderErrorCode.REMINDER_ALREADY_EXISTS, remindAt.format(JSON_DATETIME_FORMATTER));
    }

    if (!remindAt.isAfter(task.getStartDate()) || !remindAt.isBefore(task.getDueDate())) {
      throw new BaseException(
          TaskReminderErrorCode.INVALID_REMINDER, remindAt.format(JSON_DATETIME_FORMATTER));
    }

    if (remindAt.isBefore(LocalDateTime.now())) {
      throw new BaseException(
          TaskReminderErrorCode.PAST_REMINDER_NOT_ALLOWED,
          remindAt.format(JSON_DATETIME_FORMATTER));
    }
  }
}
