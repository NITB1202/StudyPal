package com.study.studypal.notification.job;

import com.study.studypal.notification.config.NotificationProperties;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceTokenCleanUpJob implements Job {
  private final DeviceTokenInternalService deviceTokenService;
  private final NotificationProperties props;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(props.getDeviceTokenCutoffDays());
    deviceTokenService.deleteDeviceTokenBefore(cutoffTime);
  }
}
