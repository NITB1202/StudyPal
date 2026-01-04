package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.internal.UpdateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.PlanTaskService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.plan.service.internal.TaskValidationService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanTaskServiceImpl implements PlanTaskService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final PlanInternalService planService;
  private final TeamMembershipInternalService memberService;
  private final TaskInternalService internalService;
  private final TaskValidationService validationService;
  private final TaskReminderInternalService reminderService;
  private final TaskNotificationService notificationService;
  private final PlanHistoryInternalService historyService;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public CreateTaskResponseDto createTaskForPlan(
      UUID userId, UUID planId, CreateTaskForPlanRequestDto request) {
    UUID assigneeId = request.getAssigneeId();
    UUID teamId = planService.getTeamIdById(planId);
    memberService.validateUpdatePlanPermission(userId, teamId);
    memberService.validateUserBelongsToTeam(assigneeId, teamId);

    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Pair<UUID, UUID> createPlanInfo = Pair.of(planId, teamId);
    Task task = internalService.createTask(assigneeId, createPlanInfo, createTaskInfo);

    reminderService.scheduleReminder(task.getDueDate(), task);
    notificationService.publishTaskAssignedNotification(userId, task);
    planService.syncPlanFromTasks(task.getPlan());
    historyService.logAssignTask(userId, assigneeId, planId, task.getTaskCode());

    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  @Override
  public UpdateTaskResponseDto updateTaskForPlan(
      UUID userId, UUID taskId, UpdateTaskForPlanRequestDto request) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    validationService.validateTeamTask(task);
    validationService.validateUpdateTaskPermission(userId, task);

    UpdateTaskInfo updateTaskInfo = modelMapper.map(request, UpdateTaskInfo.class);
    validationService.validateUpdateTaskRequest(task, updateTaskInfo);

    LocalDateTime newStartDate =
        Optional.ofNullable(request.getStartDate()).orElse(task.getStartDate());
    LocalDateTime newDueDate = Optional.ofNullable(request.getDueDate()).orElse(task.getDueDate());

    reminderService.rescheduleDueDateReminder(newDueDate, task);
    reminderService.deleteInvalidReminders(taskId, newStartDate, newDueDate);

    modelMapper.map(request, task);

    notificationService.publishTaskUpdatedNotification(userId, task);
    historyService.logUpdateTask(userId, task.getPlan().getId(), task.getTaskCode());

    UUID assigneeId = request.getAssigneeId();
    if (assigneeId != null && assigneeId != task.getAssignee().getId()) {
      UUID teamId = task.getPlan().getTeam().getId();
      memberService.validateUserBelongsToTeam(assigneeId, teamId);

      User newAssignee = entityManager.getReference(User.class, assigneeId);
      task.setAssignee(newAssignee);

      task.setCompletedAt(null);
      planService.syncPlanFromTasks(task.getPlan());

      notificationService.publishTaskAssignedNotification(userId, task);
      historyService.logAssignTask(userId, assigneeId, task.getPlan().getId(), task.getTaskCode());
    }

    taskRepository.save(task);
    return modelMapper.map(task, UpdateTaskResponseDto.class);
  }

  @Override
  public ActionResponseDto deleteTaskForPlan(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    if (task.getDeletedAt() != null) {
      throw new BaseException(TaskErrorCode.TASK_ALREADY_DELETED);
    }

    Plan plan = task.getPlan();
    UUID planId = plan.getId();
    UUID teamId = plan.getTeam().getId();

    validationService.validateTeamTask(task);
    memberService.validateUpdatePlanPermission(userId, teamId);

    int remainingTasks = taskRepository.countTasks(planId) - 1;
    Set<UUID> relatedMemberIds = planService.getPlanRelatedMemberIds(planId);

    reminderService.deleteAllRemindersForTask(taskId);
    task.setDeletedAt(LocalDateTime.now());
    taskRepository.save(task);

    if (remainingTasks == 0) {
      planService.softDeletePlan(plan);
      notificationService.publishPlanDeletedNotification(userId, plan, relatedMemberIds);
      historyService.logDeletePlan(userId, planId);
    } else {
      planService.syncPlanFromTasks(plan);
      notificationService.publishTaskDeletedNotification(userId, task);
      historyService.logDeleteTask(userId, planId, task.getTaskCode());
    }

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  @Override
  public ActionResponseDto recoverTaskForPlan(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    if (task.getDeletedAt() == null) {
      throw new BaseException(TaskErrorCode.TASK_NOT_DELETED);
    }

    Plan plan = task.getPlan();
    UUID planId = plan.getId();
    UUID teamId = plan.getTeam().getId();

    validationService.validateTeamTask(task);
    memberService.validateUpdatePlanPermission(userId, teamId);

    task.setDeletedAt(null);
    taskRepository.save(task);

    if (plan.getIsDeleted().equals(Boolean.TRUE)) {
      planService.recoverPlan(plan);
      historyService.logRecoverPlan(userId, planId);
    } else {
      historyService.logRecoverTask(userId, planId, task.getTaskCode());
    }

    planService.syncPlanFromTasks(plan);
    notificationService.publishTaskAssignedNotification(userId, task);

    return ActionResponseDto.builder().success(true).message("Recover successfully.").build();
  }
}
