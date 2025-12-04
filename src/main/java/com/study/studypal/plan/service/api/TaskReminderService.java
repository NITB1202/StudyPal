package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.reminder.request.CreateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.request.UpdateTaskReminderRequestDto;
import com.study.studypal.plan.dto.reminder.response.TaskReminderResponseDto;
import java.util.List;
import java.util.UUID;

public interface TaskReminderService {
  ActionResponseDto createReminder(UUID userId, UUID taskId, CreateTaskReminderRequestDto request);

  List<TaskReminderResponseDto> getAll(UUID userId, UUID taskId);

  ActionResponseDto updateReminder(
      UUID userId, UUID reminderId, UpdateTaskReminderRequestDto request);

  ActionResponseDto deleteReminder(UUID userId, UUID reminderId);
}
