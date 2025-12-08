package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.statistic.request.GetTaskDetailStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.request.SearchMemberTaskStatisticsRequestDto;
import com.study.studypal.plan.dto.statistic.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskDetailStatisticsResponseDto;
import com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto;
import com.study.studypal.plan.dto.task.internal.TaskStatisticsCursor;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskStatisticService;
import com.study.studypal.plan.util.TaskStatisticsCursorUtils;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskStatisticServiceImpl implements TaskStatisticService {
  private final TaskRepository taskRepository;
  private final TeamMembershipInternalService memberService;

  @Override
  public TaskDetailStatisticsResponseDto getTaskDetailStatistics(
      UUID userId, UUID teamId, GetTaskDetailStatisticsRequestDto request) {
    if (!request.getFromDate().isBefore(request.getToDate())) {
      throw new BaseException(CommonErrorCode.INVALID_DATE_RANGE);
    }

    memberService.validateUserBelongsToTeam(userId, teamId);

    List<Task> tasks =
        request.getMemberId() == null
            ? taskRepository.findAllByTeamIdInRange(
                teamId, request.getFromDate(), request.getToDate())
            : taskRepository.findAllByTeamIdAndUserIdInRange(
                teamId, request.getMemberId(), request.getFromDate(), request.getToDate());

    long unfinished = 0;
    long low = 0;
    long medium = 0;
    long high = 0;

    for (Task task : tasks) {
      if (task.getCompletedAt() == null) {
        unfinished++;
        continue;
      }

      switch (task.getPriority()) {
        case HIGH -> high++;
        case MEDIUM -> medium++;
        case LOW -> low++;
      }
    }

    return TaskDetailStatisticsResponseDto.builder()
        .total(tasks.size())
        .unfinished(unfinished)
        .high(high)
        .medium(medium)
        .low(low)
        .build();
  }

  @Override
  public ListTaskStatisticsResponseDto searchTaskStatistics(
      UUID userId, UUID teamId, SearchMemberTaskStatisticsRequestDto request) {
    LocalDateTime fromDate = request.getFromDate();
    LocalDateTime toDate = request.getToDate();

    if (!fromDate.isBefore(toDate)) {
      throw new BaseException(CommonErrorCode.INVALID_DATE_RANGE);
    }

    memberService.validateUserBelongsToTeam(userId, teamId);

    Pageable pageable = PageRequest.of(0, request.getSize());

    TaskStatisticsCursor decodedCursor =
        StringUtils.isNotBlank(request.getCursor())
            ? TaskStatisticsCursorUtils.decodeCursor(request.getCursor())
            : null;

    String handledKeyword =
        StringUtils.isNotBlank(request.getKeyword())
            ? request.getKeyword().trim().toLowerCase()
            : null;

    List<TaskStatisticsResponseDto> statistics =
        handledKeyword != null
            ? searchTaskStatistics(
                teamId, fromDate, toDate, handledKeyword, decodedCursor, pageable)
            : getTaskStatistics(teamId, fromDate, toDate, decodedCursor, pageable);

    String nextCursor =
        statistics.size() == request.getSize()
            ? TaskStatisticsCursorUtils.encodeCursor(statistics.get(statistics.size() - 1))
            : null;

    long total =
        handledKeyword != null
            ? memberService.countMembersByName(teamId, handledKeyword)
            : memberService.countMembers(teamId);

    return ListTaskStatisticsResponseDto.builder()
        .statistics(statistics)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  private List<TaskStatisticsResponseDto> searchTaskStatistics(
      UUID teamId,
      LocalDateTime fromDate,
      LocalDateTime toDate,
      String keyword,
      TaskStatisticsCursor cursor,
      Pageable pageable) {
    return cursor != null
        ? taskRepository.searchTaskStatisticsWithCursor(
            teamId,
            fromDate,
            toDate,
            keyword,
            cursor.completedTaskCount(),
            cursor.userId(),
            pageable)
        : taskRepository.searchTaskStatistics(teamId, fromDate, toDate, keyword, pageable);
  }

  private List<TaskStatisticsResponseDto> getTaskStatistics(
      UUID teamId,
      LocalDateTime fromDate,
      LocalDateTime toDate,
      TaskStatisticsCursor cursor,
      Pageable pageable) {
    return cursor != null
        ? taskRepository.getTaskStatisticsWithCursor(
            teamId, fromDate, toDate, cursor.completedTaskCount(), cursor.userId(), pageable)
        : taskRepository.getTaskStatistics(teamId, fromDate, toDate, pageable);
  }
}
