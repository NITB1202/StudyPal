package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.TASK_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskInternalServiceImpl implements TaskInternalService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;
  private final TaskCounterService taskCounterService;
  private final TaskNotificationService notificationService;
  private final TaskReminderInternalService reminderService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createTasksForPlan(PlanInfo planInfo, List<CreateTaskForPlanRequestDto> tasks) {
    for (CreateTaskForPlanRequestDto taskDto : tasks) {
      UUID assigneeId = taskDto.getAssigneeId();
      memberService.validateUserBelongsToTeam(assigneeId, planInfo.getTeamId());

      CreateTaskInfo createTaskInfo = modelMapper.map(taskDto, CreateTaskInfo.class);
      Pair<UUID, UUID> createPlanInfo = Pair.of(planInfo.getPlanId(), planInfo.getTeamId());

      Task task = createTask(assigneeId, createPlanInfo, createTaskInfo);

      reminderService.scheduleReminder(task.getDueDate(), task);
      notificationService.publishTaskAssignedNotification(planInfo.getAssignerId(), task);
    }
  }

  @Override
  public Task createTask(UUID assigneeId, Pair<UUID, UUID> planInfo, CreateTaskInfo taskInfo) {
    if (taskInfo.getDueDate().isBefore(taskInfo.getStartDate())) {
      throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, taskInfo.getContent());
    }

    UUID planId = planInfo.getLeft();
    UUID teamId = planInfo.getRight();

    User assignee = entityManager.getReference(User.class, assigneeId);
    Plan plan = planId != null ? entityManager.getReference(Plan.class, planId) : null;
    String taskCode =
        teamId != null ? generateTeamTaskCode(teamId) : generateUserTaskCode(assigneeId);

    Task task = modelMapper.map(taskInfo, Task.class);
    task.setAssignee(assignee);
    task.setPlan(plan);
    task.setTaskCode(taskCode);
    task.setIsDeleted(false);

    return taskRepository.save(task);
  }

  @Override
  public void cloneTask(UUID rootTaskId, List<LocalDate> recurrenceDates) {
    Task rootTask =
        taskRepository
            .findById(rootTaskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    List<Task> clonedTasks = new ArrayList<>();

    UUID userId = rootTask.getAssignee().getId();
    long counter = taskCounterService.getCurrentUserTaskCounter(userId);

    for (LocalDate recurrenceDate : recurrenceDates) {
      String taskCode = generateUserTaskCode(++counter);
      LocalDateTime startDate =
          LocalDateTime.of(recurrenceDate, rootTask.getStartDate().toLocalTime());
      LocalDateTime dueDate = LocalDateTime.of(recurrenceDate, rootTask.getDueDate().toLocalTime());

      Task clonedTask =
          Task.builder()
              .priority(rootTask.getPriority())
              .content(rootTask.getContent())
              .taskCode(taskCode)
              .assignee(rootTask.getAssignee())
              .startDate(startDate)
              .dueDate(dueDate)
              .parentTask(rootTask)
              .isDeleted(false)
              .build();

      clonedTasks.add(clonedTask);
    }

    taskRepository.saveAll(clonedTasks);
    taskCounterService.updateUserTaskCounter(userId, counter);

    for (Task cloneTask : clonedTasks) {
      reminderService.scheduleReminder(cloneTask.getDueDate(), cloneTask);
    }
  }

  private String generateUserTaskCode(UUID userId) {
    return TASK_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseUserTaskCounter(userId));
  }

  private String generateUserTaskCode(long counter) {
    return TASK_CODE_PREFIX + String.format(CODE_NUMBER_FORMAT, counter);
  }

  private String generateTeamTaskCode(UUID teamId) {
    return TASK_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseTeamTaskCounter(teamId));
  }

  @Override
  public Task getById(UUID id) {
    return taskRepository
        .findById(id)
        .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));
  }

  @Override
  public List<TaskResponseDto> getAll(UUID planId, boolean isDeleted) {
    List<Task> tasks =
        taskRepository.findAllByPlanIdAndIsDeletedOrderByDueDateAsc(planId, isDeleted);
    List<TaskResponseDto> responseDtoList = new ArrayList<>();

    for (Task task : tasks) {
      TaskResponseDto responseDto = modelMapper.map(task, TaskResponseDto.class);
      User assignee = task.getAssignee();

      responseDto.setAssigneeId(assignee.getId());
      responseDto.setAssigneeName(assignee.getName());
      responseDto.setAssigneeAvatarUrl(assignee.getAvatarUrl());

      responseDtoList.add(responseDto);
    }

    return responseDtoList;
  }

  @Override
  public int getTotalTasksCount(UUID planId) {
    return taskRepository.countByPlanId(planId);
  }

  @Override
  public int getCompletedTasksCount(UUID planId) {
    return taskRepository.countByPlanIdAndCompleteDateIsNotNull(planId);
  }

  @Override
  public Pair<LocalDateTime, LocalDateTime> getPlanPeriod(UUID planId) {
    List<Task> tasks = taskRepository.findAllByPlanId(planId);
    LocalDateTime startDate = null;
    LocalDateTime dueDate = null;

    for (Task task : tasks) {
      if (startDate == null || task.getStartDate().isBefore(startDate))
        startDate = task.getStartDate();

      if (dueDate == null || task.getDueDate().isAfter(dueDate)) dueDate = task.getDueDate();
    }

    return Pair.of(startDate, dueDate);
  }

  @Override
  public void validateViewTaskPermission(UUID userId, Task task) {
    Plan plan = task.getPlan();
    if (plan != null) validateUserBelongsToTeam(userId, plan);
    else validateTaskOwnership(userId, task);
  }

  @Override
  public void validateTaskOwnership(UUID userId, Task task) {
    User assignee = task.getAssignee();
    if (!userId.equals(assignee.getId()))
      throw new BaseException(TaskErrorCode.PERMISSION_TASK_OWNER_DENIED);
  }

  @Override
  public void validatePersonalTask(Task task) {
    if (task.getPlan() != null) throw new BaseException(TaskErrorCode.PERSONAL_TASK_REQUIRED);
  }

  @Override
  public void validateTeamTask(Task task) {
    if (task.getPlan() == null) throw new BaseException(TaskErrorCode.TEAM_TASK_REQUIRED);
  }

  @Override
  public void validateUpdateTaskPermission(UUID userId, Task task) {
    Plan plan = task.getPlan();
    if (plan != null) validateUpdateTaskPermission(userId, plan);
    else validateTaskOwnership(userId, task);
  }

  private void validateUserBelongsToTeam(UUID userId, Plan plan) {
    UUID teamId = plan.getTeam().getId();
    memberService.validateUserBelongsToTeam(userId, teamId);
  }

  private void validateUpdateTaskPermission(UUID userId, Plan plan) {
    UUID teamId = plan.getTeam().getId();
    memberService.validateUpdatePlanPermission(userId, teamId);
  }
}
