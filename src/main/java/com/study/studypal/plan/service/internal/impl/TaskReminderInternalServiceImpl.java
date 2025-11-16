package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.common.util.Constants.JSON_DATETIME_FORMATTER;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskReminder;
import com.study.studypal.plan.exception.TaskReminderErrorCode;
import com.study.studypal.plan.repository.TaskReminderRepository;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
public class TaskReminderInternalServiceImpl implements TaskReminderInternalService {
  private final TaskReminderRepository taskReminderRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createReminders(TaskInfo taskInfo, List<LocalDateTime> reminders) {
    if (reminders == null) return;

    List<TaskReminder> savedReminders = new ArrayList<>();
    Set<LocalDateTime> savedTimes = new HashSet<>();

    for (LocalDateTime remindAt : reminders) {
      if (savedTimes.contains(remindAt)) {
        throw new BaseException(
            TaskReminderErrorCode.REMINDER_ALREADY_EXISTS,
            remindAt.format(JSON_DATETIME_FORMATTER));
      } else {
        savedTimes.add(remindAt);
      }

      if (remindAt.isBefore(taskInfo.getStartDate()) || remindAt.isAfter(taskInfo.getDueDate())) {
        throw new BaseException(
            TaskReminderErrorCode.INVALID_REMINDER, remindAt.format(JSON_DATETIME_FORMATTER));
      }

      Task task = entityManager.getReference(Task.class, taskInfo.getId());
      TaskReminder taskReminder = TaskReminder.builder().task(task).remindAt(remindAt).build();

      savedReminders.add(taskReminder);
    }

    taskReminderRepository.saveAll(savedReminders);
  }

  @Override
  public List<LocalDateTime> getAll(UUID planId) {
    List<TaskReminder> reminders = taskReminderRepository.findAllByPlanIdOrderByRemindAtAsc(planId);
    return reminders.stream().map(TaskReminder::getRemindAt).toList();
  }
}
