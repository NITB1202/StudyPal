package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.team.exception.TeamMembershipErrorCode;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskInternalServiceImpl implements TaskInternalService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;
  private final TaskReminderInternalService reminderService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createTasksForPlan(UUID teamId, UUID planId, List<CreateTaskForPlanDto> tasks) {
    Set<String> contents = new HashSet<>();
    Plan plan = entityManager.getReference(Plan.class, planId);

    for (CreateTaskForPlanDto taskDto : tasks) {
      UUID assigneeId = taskDto.getAssigneeId();
      String content = taskDto.getContent();
      LocalDateTime startDate = taskDto.getStartDate();
      LocalDateTime dueDate = taskDto.getDueDate();

      if (contents.contains(content)) {
        throw new BaseException(TaskErrorCode.TASK_ALREADY_EXISTS, content);
      } else {
        contents.add(content);
      }

      if (dueDate.isBefore(startDate)) {
        throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, content);
      }

      if (!memberService.isUserInTeam(assigneeId, teamId)) {
        throw new BaseException(TeamMembershipErrorCode.TARGET_MEMBERSHIP_NOT_FOUND, assigneeId);
      }

      User assignee = entityManager.getReference(User.class, assigneeId);
      Task task = modelMapper.map(taskDto, Task.class);

      // Set the entity id to null to fix mapping assigneeId to id by ModelMapper.
      task.setId(null);
      task.setPlan(plan);
      task.setAssignee(assignee);

      taskRepository.save(task);

      TaskInfo taskInfo = modelMapper.map(task, TaskInfo.class);
      reminderService.createReminders(taskInfo, taskDto.getReminders());
    }
  }

  @Override
  public List<TaskResponseDto> getAll(UUID planId) {
    List<Task> tasks = taskRepository.findAllByPlanIdOrderByDueDateAsc(planId);
    List<TaskResponseDto> responseDtoList = new ArrayList<>();

    for (Task task : tasks) {
      TaskResponseDto responseDto = modelMapper.map(task, TaskResponseDto.class);
      User assignee = task.getAssignee();

      responseDto.setAssigneeId(assignee.getId());
      responseDto.setAssigneeAvatarUrl(assignee.getAvatarUrl());
      responseDto.setCompleted(task.getCompleteDate() != null);

      responseDtoList.add(responseDto);
    }

    return responseDtoList;
  }
}
