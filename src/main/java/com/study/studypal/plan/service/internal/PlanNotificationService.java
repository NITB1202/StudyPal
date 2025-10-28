package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Plan;

public interface PlanNotificationService {
  void publishPlanRemindedNotification(Plan plan);
}
