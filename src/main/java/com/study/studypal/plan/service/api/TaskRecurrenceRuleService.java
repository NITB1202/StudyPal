package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.recurrence.request.UpdateTaskRecurrenceRuleRequestDto;
import com.study.studypal.plan.dto.recurrence.response.TaskRecurrenceRuleResponseDto;
import java.util.UUID;

public interface TaskRecurrenceRuleService {
  ActionResponseDto updateRecurrenceRule(
      UUID userId, UUID taskId, UpdateTaskRecurrenceRuleRequestDto request);

  TaskRecurrenceRuleResponseDto getRecurrenceRule(UUID userId, UUID taskId);
}
