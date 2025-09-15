package com.study.studypal.notification.job;

import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceTokenCleanUpJob implements Job {
  private static final int CUTOFF_DAYS = 30;
  private final DeviceTokenInternalService deviceTokenService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(CUTOFF_DAYS);
    deviceTokenService.deleteDeviceTokenBefore(cutoffTime);
  }
}
