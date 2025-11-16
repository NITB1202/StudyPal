package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.recurrence.request.CreatePlanRecurrenceRuleDto;
import com.study.studypal.plan.dto.task.internal.TaskInfo;

public interface PlanRecurrenceRuleInternalService {
  void createPlanRecurrenceRule(TaskInfo planInfo, CreatePlanRecurrenceRuleDto ruleDto);
}
