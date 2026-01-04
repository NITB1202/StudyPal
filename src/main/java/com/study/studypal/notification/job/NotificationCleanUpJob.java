package com.study.studypal.notification.job;

import com.study.studypal.notification.config.NotificationProperties;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationCleanUpJob implements Job {
  private final NotificationInternalService notificationService;
  private final NotificationProperties props;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(props.getNotificationCutoffDays());
    notificationService.deleteNotificationBefore(cutoffTime);
  }
}
