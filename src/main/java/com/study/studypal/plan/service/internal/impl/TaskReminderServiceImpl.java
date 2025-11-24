package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.common.util.Constants.JSON_DATETIME_FORMATTER;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskReminder;
import com.study.studypal.plan.exception.TaskReminderErrorCode;
import com.study.studypal.plan.job.TaskReminderJob;
import com.study.studypal.plan.repository.TaskReminderRepository;
import com.study.studypal.plan.service.internal.TaskReminderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskReminderServiceImpl implements TaskReminderService {
  private final TaskReminderRepository taskReminderRepository;
  private final Scheduler scheduler;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createReminders(TaskInfo taskInfo, List<LocalDateTime> reminders) {
    if (reminders == null) return;

    Task task = entityManager.getReference(Task.class, taskInfo.getId());
    LocalDateTime now = LocalDateTime.now();

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

      if (!remindAt.isAfter(taskInfo.getStartDate()) || !remindAt.isBefore(taskInfo.getDueDate())) {
        throw new BaseException(
            TaskReminderErrorCode.INVALID_REMINDER, remindAt.format(JSON_DATETIME_FORMATTER));
      }

      if (remindAt.isBefore(now))
        throw new BaseException(
            TaskReminderErrorCode.PAST_REMINDER_NOT_ALLOWED,
            remindAt.format(JSON_DATETIME_FORMATTER));

      TaskReminder reminder = TaskReminder.builder().task(task).remindAt(remindAt).build();
      savedReminders.add(reminder);
    }

    TaskReminder dueDateReminder =
        TaskReminder.builder().task(task).remindAt(taskInfo.getDueDate()).build();
    savedReminders.add(dueDateReminder);

    taskReminderRepository.saveAll(savedReminders);
    scheduleReminders(savedReminders);
  }

  @Override
  public List<LocalDateTime> getAll(UUID taskId) {
    List<TaskReminder> reminders = taskReminderRepository.findAllByTaskIdOrderByRemindAtAsc(taskId);
    // Return mutable list for later changes
    return reminders.stream()
        .map(TaskReminder::getRemindAt)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private void scheduleReminders(List<TaskReminder> reminders) {
    for (TaskReminder reminder : reminders) scheduleReminder(reminder);
  }

  private void scheduleReminder(TaskReminder reminder) {
    JobDetail jobDetail =
        JobBuilder.newJob(TaskReminderJob.class)
            .withIdentity("reminder_" + reminder.getId())
            .usingJobData("taskId", reminder.getTask().getId().toString())
            .build();

    Trigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger_" + reminder.getId())
            .startAt(Timestamp.valueOf(reminder.getRemindAt()))
            .build();

    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      log.warn(e.getMessage());
      throw new BaseException(
          TaskReminderErrorCode.SCHEDULE_REMINDER_FAILED,
          reminder.getRemindAt().format(JSON_DATETIME_FORMATTER));
    }
  }
}
