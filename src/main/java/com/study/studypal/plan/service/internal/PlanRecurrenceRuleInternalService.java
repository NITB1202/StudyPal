package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.recurrence.request.CreatePlanRecurrenceRuleDto;

public interface PlanRecurrenceRuleInternalService {
  void createPlanRecurrenceRule(PlanInfo planInfo, CreatePlanRecurrenceRuleDto ruleDto);
}
