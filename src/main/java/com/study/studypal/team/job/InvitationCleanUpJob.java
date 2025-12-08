package com.study.studypal.team.job;

import static com.study.studypal.team.constant.TeamConstant.INVITATION_CUTOFF_DAYS;

import com.study.studypal.team.service.internal.InvitationInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvitationCleanUpJob implements Job {
  private final InvitationInternalService invitationService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(INVITATION_CUTOFF_DAYS);
    invitationService.deleteInvitationBefore(cutoffTime);
  }
}
