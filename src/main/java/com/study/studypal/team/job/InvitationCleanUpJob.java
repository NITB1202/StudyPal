package com.study.studypal.team.job;

import com.study.studypal.team.service.api.InvitationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InvitationCleanUpJob implements Job {
    private final InvitationService invitationService;
    private static final int CUTOFF_DAYS = 7;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffTime = now.minusDays(CUTOFF_DAYS);

    }
}
