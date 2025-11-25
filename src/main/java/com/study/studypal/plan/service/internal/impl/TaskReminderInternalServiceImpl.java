package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.plan.constant.PlanConstant.JSON_DATETIME_FORMATTER;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.entity.TaskReminder;
import com.study.studypal.plan.exception.TaskReminderErrorCode;
import com.study.studypal.plan.job.TaskReminderJob;
import com.study.studypal.plan.repository.TaskReminderRepository;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskReminderInternalServiceImpl implements TaskReminderInternalService {
  private final TaskReminderRepository taskReminderRepository;
  private final Scheduler scheduler;

  @Override
  public void scheduleReminder(LocalDateTime remindAt, Task task) {
    TaskReminder reminder = TaskReminder.builder().task(task).remindAt(remindAt).build();

    try {
      taskReminderRepository.save(reminder);
    } catch (DataIntegrityViolationException ex) {
      throw new BaseException(
          TaskReminderErrorCode.REMINDER_ALREADY_EXISTS, remindAt.format(JSON_DATETIME_FORMATTER));
    }

    scheduleReminder(reminder);
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
