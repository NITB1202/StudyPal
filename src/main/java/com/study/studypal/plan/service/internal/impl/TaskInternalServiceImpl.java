package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPersonalPlanDto;
import com.study.studypal.plan.dto.task.request.CreateTaskForTeamPlanDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.exception.TeamMembershipErrorCode;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
@RequiredArgsConstructor
public class TaskInternalServiceImpl implements TaskInternalService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createTasksForPersonalPlan(
      UUID userId, PlanInfo planInfo, List<CreateTaskForPersonalPlanDto> tasks) {
    List<Task> savedTasks = new ArrayList<>();
    Set<String> contents = new HashSet<>();

    Plan plan = entityManager.getReference(Plan.class, planInfo.getId());
    User user = entityManager.getReference(User.class, userId);

    for (CreateTaskForPersonalPlanDto taskDto : tasks) {
      String content = taskDto.getContent();
      LocalDateTime dueDate = taskDto.getDueDate();

      if (contents.contains(content)) {
        throw new BaseException(TaskErrorCode.TASK_ALREADY_EXISTS, content);
      } else {
        contents.add(content);
      }

      if (dueDate.isBefore(planInfo.getStartDate()) || dueDate.isAfter(planInfo.getDueDate())) {
        throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, content);
      }

      Task task =
          Task.builder().plan(plan).assignee(user).content(content).dueDate(dueDate).build();

      savedTasks.add(task);
    }

    taskRepository.saveAll(savedTasks);
  }

  @Override
  public void createTasksForTeamPlan(
      UUID teamId, PlanInfo planInfo, List<CreateTaskForTeamPlanDto> tasks) {
    List<Task> savedTasks = new ArrayList<>();
    Set<String> contents = new HashSet<>();

    Plan plan = entityManager.getReference(Plan.class, planInfo.getId());

    for (CreateTaskForTeamPlanDto taskDto : tasks) {
      String content = taskDto.getContent();
      LocalDateTime dueDate = taskDto.getDueDate();

      if (contents.contains(content)) {
        throw new BaseException(TaskErrorCode.TASK_ALREADY_EXISTS, content);
      } else {
        contents.add(content);
      }

      if (dueDate.isBefore(planInfo.getStartDate()) || dueDate.isAfter(planInfo.getDueDate())) {
        throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, content);
      }

      if (!memberService.isUserInTeam(taskDto.getAssigneeId(), teamId)) {
        throw new BaseException(TeamMembershipErrorCode.TARGET_MEMBERSHIP_NOT_FOUND);
      }

      User assignee = entityManager.getReference(User.class, taskDto.getAssigneeId());

      Task task =
          Task.builder().plan(plan).assignee(assignee).content(content).dueDate(dueDate).build();

      savedTasks.add(task);
    }

    taskRepository.saveAll(savedTasks);
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
