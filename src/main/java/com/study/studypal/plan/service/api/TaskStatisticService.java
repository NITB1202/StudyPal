package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.task.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailStatisticsResponseDto;
import java.util.UUID;

public interface TaskStatisticService {
  TaskDetailStatisticsResponseDto getTaskDetailStatistics(UUID userId, UUID teamId, UUID memberId);

  ListTaskStatisticsResponseDto searchMembers(UUID teamId, String keyword, String cursor, int size);
}
