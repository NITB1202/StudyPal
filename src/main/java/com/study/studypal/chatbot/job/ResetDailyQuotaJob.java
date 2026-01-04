package com.study.studypal.chatbot.job;

import com.study.studypal.chatbot.service.internal.UserQuotaService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetDailyQuotaJob implements Job {
  private final UserQuotaService quotaService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    quotaService.resetDailyQuotaForAllUsers();
  }
}
