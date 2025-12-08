package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.statistic.request.GetTaskDetailStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.request.SearchMemberTaskStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskDetailStatisticsResponseDto;
import java.util.UUID;

public interface TaskStatisticService {
  TaskDetailStatisticsResponseDto getTaskDetailStatistics(
      UUID userId, UUID teamId, GetTaskDetailStatisticsRequestDto request);

  ListTaskStatisticsResponseDto searchTaskStatistics(
      UUID userId, UUID teamId, SearchMemberTaskStatisticsRequestDto request);
}
