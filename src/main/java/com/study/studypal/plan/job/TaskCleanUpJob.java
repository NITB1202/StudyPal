package com.study.studypal.plan.job;

import static com.study.studypal.plan.constant.PlanConstant.TASK_CUTOFF_DAYS;

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

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime cutoffTime = now.minusDays(TASK_CUTOFF_DAYS);
    taskService.hardDeleteTasksBefore(cutoffTime);
  }
}
