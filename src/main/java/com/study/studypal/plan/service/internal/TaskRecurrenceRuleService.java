package com.study.studypal.plan.service.internal;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.recurrence.request.CreateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import java.util.UUID;

public interface TaskRecurrenceRuleService {
  ActionResponseDto createRecurrenceRule(
      UUID userId, UUID taskId, CreateTaskRecurrenceRuleRequestDto request);

  TaskRecurrenceRuleResponseDto getRecurrenceRule(UUID userId, UUID taskId);
}
