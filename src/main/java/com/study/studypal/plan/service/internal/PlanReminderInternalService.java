package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PlanReminderInternalService {
  void createReminders(PlanInfo planInfo, List<LocalDateTime> reminders);

  List<LocalDateTime> getAll(UUID planId);
}
