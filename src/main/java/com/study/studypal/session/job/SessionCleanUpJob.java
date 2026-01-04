package com.study.studypal.session.job;

import com.study.studypal.session.config.SessionProperties;
import com.study.studypal.session.service.SessionInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanUpJob implements Job {
  private final SessionInternalService sessionService;
  private final SessionProperties properties;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(properties.getSessionCutoffDays());
    sessionService.deleteSessionsBefore(cutoffTime);
  }
}
