package com.study.studypal.team.job;

import com.study.studypal.team.config.TeamProperties;
import com.study.studypal.team.service.internal.InvitationInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvitationCleanUpJob implements Job {
  private final InvitationInternalService invitationService;
  private final TeamProperties props;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(props.getInvitationCutoffDays());
    invitationService.deleteInvitationBefore(cutoffTime);
  }
}
