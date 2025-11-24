package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;
  private final PlanInternalService planService;
  private final TaskNotificationService notificationService;
  private final TaskInternalService internalService;
  private final PlanHistoryInternalService historyService;

  @Override
  @Transactional
  public CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request) {
    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Task task = internalService.createTask(userId, Pair.of(null, null), createTaskInfo);
    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  @Override
  @Transactional
  public CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request) {
    UUID assigneeId = request.getAssigneeId();
    UUID teamId = planService.getTeamIdById(planId);
    memberService.validateUpdatePlanPermission(userId, teamId);
    memberService.validateUserBelongsToTeam(assigneeId, teamId);

    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Pair<UUID, UUID> createPlanInfo = Pair.of(planId, teamId);
    Task task = internalService.createTask(assigneeId, createPlanInfo, createTaskInfo);

    notificationService.publishTaskAssignedNotification(userId, task);
    planService.updatePlanProgress(planId);
    historyService.logAssignTask(userId, assigneeId, planId, task.getTaskCode());

    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  @Override
  public TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    Plan plan = task.getPlan();
    User assignee = task.getAssignee();
    if (plan != null) validateUserBelongsToTeam(userId, plan);
    else validateTaskOwnership(userId, assignee);

    TaskDetailResponseDto response = modelMapper.map(task, TaskDetailResponseDto.class);
    response.setAssigneeId(assignee.getId());
    response.setAssigneeAvatarUrl(assignee.getAvatarUrl());

    return response;
  }

  private void validateUserBelongsToTeam(UUID userId, Plan plan) {
    UUID teamId = plan.getTeam().getId();
    memberService.validateUserBelongsToTeam(userId, teamId);
  }

  private void validateTaskOwnership(UUID userId, User assignee) {
    if (!userId.equals(assignee.getId())) {
      throw new BaseException(TaskErrorCode.PERMISSION_VIEW_TASK_DENIED);
    }
  }
}
