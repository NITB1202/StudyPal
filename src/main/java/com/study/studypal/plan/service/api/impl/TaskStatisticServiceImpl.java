package com.study.studypal.plan.service.api.impl;

import com.study.studypal.plan.dto.task.response.ListTaskStatisticsResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailStatisticsResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskStatisticService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskStatisticServiceImpl implements TaskStatisticService {
  private final TaskRepository taskRepository;
  private final TeamMembershipInternalService memberService;

  @Override
  public TaskDetailStatisticsResponseDto getTaskDetailStatistics(
      UUID userId, UUID teamId, UUID memberId) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    List<Task> tasks =
        memberId == null
            ? taskRepository.findAllByTeamId(teamId)
            : taskRepository.findAllByTeamIdAndUserId(teamId, memberId);

    long unfinished = 0;
    long low = 0;
    long medium = 0;
    long high = 0;

    for (Task task : tasks) {
      if (task.getCompleteDate() == null) {
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
  public ListTaskStatisticsResponseDto searchMembers(
      UUID teamId, String keyword, String cursor, int size) {

    return null;
  }
}
