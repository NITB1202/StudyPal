package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.reminder.request.CreateReminderDto;
import java.util.List;

public interface PlanReminderInternalService {
  void createRemindersForPersonalPlan(PlanInfo planInfo, List<CreateReminderDto> reminderDtoList);
}
