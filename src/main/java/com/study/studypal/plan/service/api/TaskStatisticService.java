package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.statistic.request.GetTeamTaskDetailStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.request.SearchMemberTaskStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskDetailStatisticsResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TaskStatisticService {
  TaskDetailStatisticsResponseDto getTeamTaskDetailStatistics(
      UUID userId, UUID teamId, GetTeamTaskDetailStatisticsRequestDto request);

  ListTaskStatisticsResponseDto searchMemberTaskStatistics(
      UUID userId, UUID teamId, SearchMemberTaskStatisticsRequestDto request);

  TaskDetailStatisticsResponseDto getTaskDetailStatistics(
      UUID userId, LocalDateTime fromDate, LocalDateTime toDate);
}
