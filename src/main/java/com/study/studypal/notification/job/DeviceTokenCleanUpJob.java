package com.study.studypal.notification.job;

import static com.study.studypal.notification.constant.NotificationConstant.DEVICE_TOKEN_CUTOFF_DAYS;

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
  private final DeviceTokenInternalService deviceTokenService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(DEVICE_TOKEN_CUTOFF_DAYS);
    deviceTokenService.deleteDeviceTokenBefore(cutoffTime);
  }
}
