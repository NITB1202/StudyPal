package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanReminderInternalService {
  void createRemindersForPersonalPlan(PlanInfo planInfo, List<LocalDateTime> reminderTimes);
}
