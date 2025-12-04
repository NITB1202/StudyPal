package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.DateErrorCode;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.internal.UpdateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.DeletedTaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.ListDeletedTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskAdditionalDataResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.enums.ApplyScope;
import com.study.studypal.plan.enums.TaskType;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleInternalService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;
  private final PlanInternalService planService;
  private final TaskNotificationService notificationService;
  private final TaskInternalService internalService;
  private final PlanHistoryInternalService historyService;
  private final TaskReminderInternalService reminderService;
  private final TaskRecurrenceRuleInternalService ruleService;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request) {
    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Task task = internalService.createTask(userId, Pair.of(null, null), createTaskInfo);
    reminderService.scheduleReminder(task.getDueDate(), task);
    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

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

    internalService.validateViewTaskPermission(userId, task);
    TaskDetailResponseDto response = modelMapper.map(task, TaskDetailResponseDto.class);
    response.setTaskType(getTaskType(task));

    Plan plan = task.getPlan();
    User assignee = task.getAssignee();

    if (plan != null) {
      TaskAdditionalDataResponseDto additionalData = buildTaskAdditionalData(plan, assignee);
      response.setAdditionalData(additionalData);
    }

    return response;
  }

  @Override
  public List<TaskSummaryResponseDto> getAssignedTasksOnDate(UUID userId, LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<Task> tasks = taskRepository.getAssignedTasksOnDate(userId, startOfDay, endOfDay);

    return tasks.stream()
        .map(
            t -> {
              TaskSummaryResponseDto summary = modelMapper.map(t, TaskSummaryResponseDto.class);
              summary.setTaskType(getTaskType(t));
              return summary;
            })
        .toList();
  }

  @Override
  public List<String> getDatesWithTaskDueDateInMonth(UUID userId, Integer month, Integer year) {
    LocalDate now = LocalDate.now();
    int handledMonth = month == null ? now.getMonthValue() : month;
    int handledYear = year == null ? now.getYear() : year;

    if (handledMonth < 1 || handledMonth > 12) {
      throw new BaseException(DateErrorCode.INVALID_M0NTH);
    }
    if (handledYear <= 0) {
      throw new BaseException(DateErrorCode.INVALID_YEAR);
    }

    List<LocalDateTime> dueDates =
        taskRepository.findTaskDueDatesByUserIdInMonth(userId, handledMonth, handledYear);

    return dueDates.stream().map(d -> d.toLocalDate().toString()).distinct().sorted().toList();
  }

  @Override
  public ListDeletedTaskResponseDto getDeletedTasks(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    if (teamId != null) {
      memberService.validateUserBelongsToTeam(userId, teamId);
    }

    List<Task> tasks =
        teamId != null
            ? getTeamDeletedTasks(teamId, cursor, pageable)
            : getPersonalDeletedTasks(userId, cursor, pageable);

    List<DeletedTaskSummaryResponseDto> tasksDTO =
        tasks.stream()
            .map(
                task -> {
                  DeletedTaskSummaryResponseDto taskDto =
                      modelMapper.map(task, DeletedTaskSummaryResponseDto.class);
                  if (teamId != null) {
                    taskDto.setPlanCode(task.getPlan().getPlanCode());
                  }
                  return taskDto;
                })
            .toList();

    long total =
        teamId != null
            ? taskRepository.countTeamDeletedTasks(teamId)
            : taskRepository.countPersonalDeletedTasks(userId);

    LocalDateTime nextCursor =
        !tasks.isEmpty() && tasks.size() == size
            ? tasks.get(tasks.size() - 1).getDeletedAt()
            : null;

    return ListDeletedTaskResponseDto.builder()
        .tasks(tasksDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  private List<Task> getPersonalDeletedTasks(UUID userId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? taskRepository.getPersonalDeletedTasks(userId, pageable)
        : taskRepository.getPersonalDeletedTasksWithCursor(userId, cursor, pageable);
  }

  private List<Task> getTeamDeletedTasks(UUID teamId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? taskRepository.getTeamDeletedTasks(teamId, pageable)
        : taskRepository.getTeamDeletedTasksWithCursor(teamId, cursor, pageable);
  }

  @Override
  public UpdateTaskResponseDto updateTask(
      UUID userId, UUID taskId, ApplyScope applyScope, UpdateTaskRequestDto request) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    internalService.validatePersonalTask(task);
    internalService.validateTaskOwnership(userId, task);

    UpdateTaskInfo updateTaskInfo = modelMapper.map(request, UpdateTaskInfo.class);
    validateUpdateTaskRequest(task, updateTaskInfo);

    if (ruleService.isRootOrClonedTask(task)) updateClonedTask(applyScope, task, request);
    else updatePersonalTask(task, request);

    return modelMapper.map(task, UpdateTaskResponseDto.class);
  }

  @Override
  public UpdateTaskResponseDto updateTaskForPlan(
      UUID userId, UUID taskId, UpdateTaskForPlanRequestDto request) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    internalService.validateTeamTask(task);
    internalService.validateUpdateTaskPermission(userId, task);

    UpdateTaskInfo updateTaskInfo = modelMapper.map(request, UpdateTaskInfo.class);
    validateUpdateTaskRequest(task, updateTaskInfo);

    LocalDateTime newStartDate =
        request.getStartDate() != null ? request.getStartDate() : task.getStartDate();
    LocalDateTime newDueDate =
        request.getDueDate() != null ? request.getDueDate() : task.getDueDate();

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

      task.setCompleteDate(null);
      planService.updatePlanProgress(task.getPlan().getId());

      notificationService.publishTaskAssignedNotification(userId, task);
      historyService.logAssignTask(userId, assigneeId, task.getPlan().getId(), task.getTaskCode());
    }

    taskRepository.save(task);
    return modelMapper.map(task, UpdateTaskResponseDto.class);
  }

  @Override
  public ActionResponseDto markTaskAsCompleted(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    if (task.getDeletedAt() != null) throw new BaseException(TaskErrorCode.TASK_ALREADY_DELETED);

    if (task.getCompleteDate() != null)
      throw new BaseException(TaskErrorCode.TASK_ALREADY_COMPLETED);

    if (!task.getAssignee().getId().equals(userId))
      throw new BaseException(TaskErrorCode.TASK_ASSIGNEE_ONLY);

    task.setCompleteDate(LocalDateTime.now());
    taskRepository.save(task);

    Plan plan = task.getPlan();

    if (plan != null) {
      float planProgress = planService.updatePlanProgress(plan.getId());
      if (planProgress >= 1.0f) notificationService.publishPlanCompletedNotification(plan);
      historyService.logCompleteTask(userId, plan.getId(), task.getTaskCode());
    }

    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  public ActionResponseDto deleteTask(UUID userId, UUID taskId, ApplyScope applyScope) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    internalService.validatePersonalTask(task);
    internalService.validateTaskOwnership(userId, task);

    if (task.getDeletedAt() != null) throw new BaseException(TaskErrorCode.TASK_ALREADY_DELETED);

    if (ruleService.isRootOrClonedTask(task)) deleteClonedTask(task, applyScope);
    else deletePersonalTask(task);

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  @Override
  public ActionResponseDto deleteTaskForPlan(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    if (task.getDeletedAt() != null) throw new BaseException(TaskErrorCode.TASK_ALREADY_DELETED);

    Plan plan = task.getPlan();
    UUID planId = plan.getId();
    UUID teamId = plan.getTeam().getId();

    internalService.validateTeamTask(task);
    memberService.validateUpdatePlanPermission(userId, teamId);

    reminderService.deleteAllRemindersForTask(taskId);
    notificationService.publishTaskDeletedNotification(userId, task);
    historyService.logDeleteTask(userId, planId, task.getTaskCode());

    task.setDeletedAt(LocalDateTime.now());
    taskRepository.save(task);

    planService.updatePlanProgress(planId);

    int remainingTasks = taskRepository.countTasks(planId);

    if (remainingTasks == 0) {
      planService.softDeletePlan(plan);
      notificationService.publishPlanDeletedNotification(userId, plan);
      historyService.logDeletePlan(userId, planId);
    }

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  private TaskAdditionalDataResponseDto buildTaskAdditionalData(Plan plan, User assignee) {
    return TaskAdditionalDataResponseDto.builder()
        .planId(plan.getId())
        .planCode(plan.getPlanCode())
        .assigneeId(assignee.getId())
        .assigneeName(assignee.getName())
        .assigneeAvatarUrl(assignee.getAvatarUrl())
        .build();
  }

  private TaskType getTaskType(Task task) {
    if (task.getPlan() != null) return TaskType.TEAM;
    if (ruleService.isRootOrClonedTask(task)) return TaskType.CLONED;
    return TaskType.PERSONAL;
  }

  private void validateUpdateTaskRequest(Task task, UpdateTaskInfo info) {
    if (info.getContent() != null && info.getContent().isBlank())
      throw new BaseException(TaskErrorCode.BLANK_TASK);

    LocalDateTime startDate =
        info.getStartDate() != null ? info.getStartDate() : task.getStartDate();
    LocalDateTime dueDate = info.getDueDate() != null ? info.getDueDate() : task.getDueDate();

    if (dueDate.isBefore(startDate))
      throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, task.getContent());
  }

  private List<Task> findAllActiveTasksIncludingOriginal(Task task) {
    Task rootTask = task.getParentTask();
    List<Task> tasks = taskRepository.findAllActiveChildTasks(rootTask.getId());
    tasks.add(0, rootTask);
    return tasks;
  }

  private void updatePersonalTask(Task task, UpdateTaskRequestDto request) {
    LocalDateTime newStartDate =
        request.getStartDate() != null ? request.getStartDate() : task.getStartDate();
    LocalDateTime newDueDate =
        request.getDueDate() != null ? request.getDueDate() : task.getDueDate();

    reminderService.rescheduleDueDateReminder(newDueDate, task);
    reminderService.deleteInvalidReminders(task.getId(), newStartDate, newDueDate);

    modelMapper.map(request, task);
    taskRepository.save(task);
  }

  private void updateClonedTask(ApplyScope applyScope, Task task, UpdateTaskRequestDto request) {
    LocalDateTime newStartDate =
        request.getStartDate() != null ? request.getStartDate() : task.getStartDate();
    LocalDateTime newDueDate =
        request.getDueDate() != null ? request.getDueDate() : task.getDueDate();

    ruleService.validateClonedTaskDuration(newStartDate, newDueDate);

    List<Task> tasksToUpdate =
        applyScope.equals(ApplyScope.CURRENT_ONLY)
            ? List.of(task)
            : findAllActiveTasksIncludingOriginal(task);

    Duration startDateDelta = Duration.between(task.getStartDate(), newStartDate);
    Duration dueDateDelta = Duration.between(task.getDueDate(), newDueDate);

    for (Task taskToUpdate : tasksToUpdate) {
      LocalDateTime updatedStartDate = taskToUpdate.getStartDate().plus(startDateDelta);
      LocalDateTime updatedDueDate = taskToUpdate.getDueDate().plus(dueDateDelta);

      reminderService.rescheduleDueDateReminder(updatedDueDate, taskToUpdate);
      reminderService.deleteInvalidReminders(
          taskToUpdate.getId(), updatedStartDate, updatedDueDate);

      modelMapper.map(request, taskToUpdate);

      taskToUpdate.setStartDate(updatedStartDate);
      taskToUpdate.setDueDate(updatedDueDate);
    }

    taskRepository.saveAll(tasksToUpdate);
  }

  private void deletePersonalTask(Task task) {
    reminderService.deleteAllRemindersForTask(task.getId());
    task.setDeletedAt(LocalDateTime.now());
    taskRepository.save(task);
  }

  private void deleteClonedTask(Task task, ApplyScope applyScope) {
    List<Task> tasksToUpdate =
        applyScope.equals(ApplyScope.CURRENT_ONLY)
            ? List.of(task)
            : findAllActiveTasksIncludingOriginal(task);

    for (Task taskToUpdate : tasksToUpdate) {
      reminderService.deleteAllRemindersForTask(taskToUpdate.getId());
      taskToUpdate.setDeletedAt(LocalDateTime.now());
    }

    taskRepository.saveAll(tasksToUpdate);
  }
}
