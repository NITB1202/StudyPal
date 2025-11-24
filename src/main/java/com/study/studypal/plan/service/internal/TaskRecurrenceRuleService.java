package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.task.internal.TaskInfo;

public interface TaskRecurrenceRuleService {
  void createRecurrenceRule(TaskInfo taskInfo, CreateTaskRecurrenceRuleRequestDto request);
}
