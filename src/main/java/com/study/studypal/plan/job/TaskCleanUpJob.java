package com.study.studypal.plan.job;

import com.study.studypal.plan.config.PlanProperties;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskCleanUpJob implements Job {
  private final TaskInternalService taskService;
  private final PlanInternalService planService;
  private final PlanProperties props;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(props.getTaskCutoffDays());
    taskService.hardDeleteTasksBefore(cutoffTime);
    planService.purgeEmptySoftDeletedPlans();
  }
}
