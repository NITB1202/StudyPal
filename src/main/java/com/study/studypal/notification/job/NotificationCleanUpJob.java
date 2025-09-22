package com.study.studypal.notification.job;

import com.study.studypal.notification.service.internal.NotificationInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationCleanUpJob implements Job {
  private final NotificationInternalService notificationService;
  private static final int CUTOFF_DAYS = 7;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(CUTOFF_DAYS);
    notificationService.deleteNotificationBefore(cutoffTime);
  }
}
