package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.TASK_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskReminderService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TaskCounterService taskCounterService;
  private final TaskReminderService reminderService;
  private final TeamMembershipInternalService memberService;
  private final PlanInternalService planService;
  private final TaskNotificationService notificationService;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request) {
    if (request.getDueDate().isBefore(request.getStartDate())) {
      throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, request.getContent());
    }

    String taskCode = generateUserTaskCode(userId);
    User user = entityManager.getReference(User.class, userId);

    Task task =
        Task.builder()
            .taskCode(taskCode)
            .content(request.getContent())
            .startDate(request.getStartDate())
            .dueDate(request.getDueDate())
            .note(request.getNote())
            .priority(request.getPriority())
            .assignee(user)
            .build();

    taskRepository.save(task);

    TaskInfo taskInfo = modelMapper.map(task, TaskInfo.class);
    reminderService.createReminders(taskInfo, request.getReminders());

    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  @Override
  @Transactional
  public CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request) {
    UUID teamId = planService.getTeamIdById(planId);
    memberService.validateUpdatePlanPermission(userId, teamId);
    memberService.validateUserBelongsToTeam(request.getAssigneeId(), teamId);

    if (request.getDueDate().isBefore(request.getStartDate())) {
      throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, request.getContent());
    }

    String taskCode = generateTeamTaskCode(teamId);
    User assignee = entityManager.getReference(User.class, request.getAssigneeId());
    Plan plan = entityManager.getReference(Plan.class, planId);

    Task task =
        Task.builder()
            .taskCode(taskCode)
            .content(request.getContent())
            .startDate(request.getStartDate())
            .dueDate(request.getDueDate())
            .note(request.getNote())
            .priority(request.getPriority())
            .assignee(assignee)
            .plan(plan)
            .build();

    taskRepository.save(task);

    TaskInfo taskInfo = modelMapper.map(task, TaskInfo.class);
    reminderService.createReminders(taskInfo, request.getReminders());
    notificationService.publishTaskAssignedNotification(userId, task);

    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  private String generateUserTaskCode(UUID userId) {
    return TASK_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseUserTaskCounter(userId));
  }

  private String generateTeamTaskCode(UUID teamId) {
    return TASK_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseTeamTaskCounter(teamId));
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

    List<LocalDateTime> reminders = reminderService.getAll(taskId);
    reminders.remove(task.getDueDate());
    response.setReminders(reminders);

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
