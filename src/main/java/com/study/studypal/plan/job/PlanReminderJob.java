package com.study.studypal.plan.job;

import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.PlanNotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanReminderJob implements Job {
  private final PlanInternalService planService;
  private final PlanNotificationService planNotificationService;

  @Override
  public void execute(JobExecutionContext context) {
    JobDataMap dataMap = context.getMergedJobDataMap();

    UUID planId = UUID.fromString(dataMap.getString("planId"));
    Plan plan = planService.getById(planId);

    // If plan is completed, doesn't need to send notification
    if (plan.getProgress() == 1f) return;

    planNotificationService.publishPlanRemindedNotification(plan);
  }
}
