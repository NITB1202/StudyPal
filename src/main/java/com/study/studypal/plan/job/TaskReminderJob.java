package com.study.studypal.plan.job;

import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskReminderJob implements Job {
  private final TaskInternalService taskService;
  private final TaskNotificationService notificationService;

  @Override
  public void execute(JobExecutionContext context) {
    JobDataMap dataMap = context.getMergedJobDataMap();

    UUID taskId = UUID.fromString(dataMap.getString("taskId"));
    Task task = taskService.getById(taskId);

    // If task is completed, doesn't need to send notification
    if (task.getCompleteDate() != null) return;

    notificationService.publishTaskRemindedNotification(task);
  }
}
