package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.plan.constant.PlanConstant.SAFE_MAX_DATE_TIME;
import static com.study.studypal.plan.constant.PlanConstant.SAFE_MIN_DATE_TIME;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.internal.TaskCursor;
import com.study.studypal.plan.dto.task.internal.UpdateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.SearchTasksRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.DeletedTaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.ListDeletedTaskResponseDto;
import com.study.studypal.plan.dto.task.response.ListTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskAdditionalDataResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.enums.ApplyScope;
import com.study.studypal.plan.enums.TaskType;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.mapper.TaskMapper;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleInternalService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.plan.service.internal.TaskValidationService;
import com.study.studypal.plan.util.TaskCursorUtils;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
  private final TaskMapper taskMapper;
  private final TeamMembershipInternalService memberService;
  private final PlanInternalService planService;
  private final TaskNotificationService notificationService;
  private final TaskInternalService internalService;
  private final TaskValidationService validationService;
  private final PlanHistoryInternalService historyService;
  private final TaskReminderInternalService reminderService;
  private final TaskRecurrenceRuleInternalService ruleService;

  @Override
  public CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request) {
    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Task task = internalService.createTask(userId, Pair.of(null, null), createTaskInfo);
    reminderService.scheduleReminder(task.getDueDate(), task);
    return modelMapper.map(task, CreateTaskResponseDto.class);
  }

  @Override
  public TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    validationService.validateViewTaskPermission(userId, task);

    TaskType taskType = getTaskType(task);
    TaskDetailResponseDto response = taskMapper.toTaskDetailResponseDto(task, taskType);

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
    return toTaskSummaryResponseDtoList(tasks);
  }

  @Override
  public List<String> getDatesWithTaskDueDateInMonth(UUID userId, Integer month, Integer year) {
    LocalDate now = LocalDate.now();
    int handledMonth = Optional.ofNullable(month).orElse(now.getMonthValue());
    int handledYear = Optional.ofNullable(year).orElse(now.getYear());

    if (handledMonth < 1 || handledMonth > 12) {
      throw new BaseException(CommonErrorCode.INVALID_M0NTH);
    }
    if (handledYear <= 0) {
      throw new BaseException(CommonErrorCode.INVALID_YEAR);
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

    List<DeletedTaskSummaryResponseDto> tasksResponseDto =
        tasks.stream()
            .map(
                task -> {
                  DeletedTaskSummaryResponseDto taskDto =
                      modelMapper.map(task, DeletedTaskSummaryResponseDto.class);
                  taskDto.setTaskType(getTaskType(task));
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
        tasks.size() == size ? tasks.get(tasks.size() - 1).getDeletedAt() : null;

    return ListDeletedTaskResponseDto.builder()
        .tasks(tasksResponseDto)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public ListTaskResponseDto searchTasks(UUID userId, SearchTasksRequestDto request) {
    LocalDateTime fromDate = Optional.ofNullable(request.getFromDate()).orElse(SAFE_MIN_DATE_TIME);
    LocalDateTime toDate = Optional.ofNullable(request.getToDate()).orElse(SAFE_MAX_DATE_TIME);

    if (!fromDate.isBefore(toDate)) {
      throw new BaseException(CommonErrorCode.INVALID_TIME_RANGE);
    }

    Pageable pageable = PageRequest.of(0, request.getSize());
    String handledKeyword = request.getKeyword().trim().toLowerCase();

    List<Task> tasks;
    if (request.getCursor() != null && !request.getCursor().isEmpty()) {
      TaskCursor decodedCursor = TaskCursorUtils.decodeCursor(request.getCursor());
      tasks =
          taskRepository.searchTasksWithCursor(
              userId, handledKeyword, fromDate, toDate, decodedCursor, pageable);
    } else {
      tasks = taskRepository.searchTasks(userId, handledKeyword, fromDate, toDate, pageable);
    }

    List<TaskSummaryResponseDto> tasksDTO = toTaskSummaryResponseDtoList(tasks);
    long total = taskRepository.countTasks(userId, handledKeyword, fromDate, toDate);

    String nextCursor = null;
    if (tasks.size() == request.getSize()) {
      Task lastTask = tasks.get(tasks.size() - 1);
      nextCursor = TaskCursorUtils.encodeCursor(lastTask);
    }

    return ListTaskResponseDto.builder()
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

    validationService.validateTaskIsIncomplete(task);
    validationService.validatePersonalTask(task);
    validationService.validateTaskOwnership(userId, task);
    validationService.validateTaskNotDeleted(task);

    UpdateTaskInfo updateTaskInfo = modelMapper.map(request, UpdateTaskInfo.class);
    validationService.validateUpdateTaskRequest(task, updateTaskInfo);

    if (ruleService.isRootOrClonedTask(task)) {
      updateClonedTask(applyScope, task, request);
    } else {
      updatePersonalTask(task, request);
    }

    return modelMapper.map(task, UpdateTaskResponseDto.class);
  }

  @Override
  public ActionResponseDto markTaskAsCompleted(UUID userId, UUID taskId) {
    Task task =
        taskRepository
            .findByIdForUpdate(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    validationService.validateTaskNotDeleted(task);
    validationService.validateTaskIsIncomplete(task);

    if (!task.getAssignee().getId().equals(userId)) {
      throw new BaseException(TaskErrorCode.TASK_ASSIGNEE_ONLY);
    }

    task.setCompletedAt(LocalDateTime.now());
    taskRepository.save(task);

    Plan plan = task.getPlan();

    if (plan != null) {
      planService.syncPlanFromTasks(plan);
      if (plan.getProgress() >= 1.0f) notificationService.publishPlanCompletedNotification(plan);
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

    validationService.validatePersonalTask(task);
    validationService.validateTaskOwnership(userId, task);
    validationService.validateTaskNotDeleted(task);

    if (ruleService.isRootOrClonedTask(task)) {
      deleteClonedTask(task, applyScope);
    } else {
      deletePersonalTask(task);
    }

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  @Override
  public ActionResponseDto recoverTask(UUID userId, UUID taskId, ApplyScope applyScope) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));

    validationService.validatePersonalTask(task);
    validationService.validateTaskOwnership(userId, task);
    validationService.validateTaskDeleted(task);

    if (ruleService.isRootOrClonedTask(task)) {
      recoverClonedTask(task, applyScope);
    } else {
      recoverPersonalTask(task);
    }

    return ActionResponseDto.builder().success(true).message("Recover successfully.").build();
  }

  private TaskAdditionalDataResponseDto buildTaskAdditionalData(Plan plan, User assignee) {
    UUID teamId = plan.getTeam().getId();
    TeamRole role = memberService.getTeamRole(assignee.getId(), teamId);

    return TaskAdditionalDataResponseDto.builder()
        .role(role)
        .planId(plan.getId())
        .planCode(plan.getPlanCode())
        .assigneeId(assignee.getId())
        .assigneeName(assignee.getName())
        .assigneeAvatarUrl(assignee.getAvatarUrl())
        .build();
  }

  private List<Task> getClonedTasks(Task task, ApplyScope applyScope) {
    if (applyScope == null) {
      throw new BaseException(TaskErrorCode.TASK_SCOPE_REQUIRED);
    }

    if (applyScope.equals(ApplyScope.CURRENT_ONLY)) {
      return List.of(task);
    }

    return task.getDeletedAt() != null
        ? findAllDeletedClonedTasks(task)
        : internalService.getAllActiveClonedTasksIncludingOriginal(task);
  }

  private List<Task> findAllDeletedClonedTasks(Task task) {
    Task rootTask = task.getParentTask() != null ? task.getParentTask() : task;
    List<Task> tasks = taskRepository.findAllDeletedChildTasks(rootTask.getId());
    tasks.add(0, rootTask);
    return tasks;
  }

  private void updatePersonalTask(Task task, UpdateTaskRequestDto request) {
    LocalDateTime newStartDate =
        Optional.ofNullable(request.getStartDate()).orElse(task.getStartDate());
    LocalDateTime newDueDate = Optional.ofNullable(request.getDueDate()).orElse(task.getDueDate());

    reminderService.rescheduleDueDateReminder(newDueDate, task);
    reminderService.deleteInvalidReminders(task.getId(), newStartDate, newDueDate);

    modelMapper.map(request, task);
    taskRepository.save(task);
  }

  private void updateClonedTask(ApplyScope applyScope, Task task, UpdateTaskRequestDto request) {
    LocalDateTime newStartDate =
        Optional.ofNullable(request.getStartDate()).orElse(task.getStartDate());
    LocalDateTime newDueDate = Optional.ofNullable(request.getDueDate()).orElse(task.getDueDate());

    ruleService.validateClonedTaskDuration(newStartDate, newDueDate);

    List<Task> tasksToUpdate = getClonedTasks(task, applyScope);

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
    List<Task> tasksToDelete = getClonedTasks(task, applyScope);

    for (Task taskToDelete : tasksToDelete) {
      reminderService.deleteAllRemindersForTask(taskToDelete.getId());
      taskToDelete.setDeletedAt(LocalDateTime.now());
    }

    taskRepository.saveAll(tasksToDelete);
  }

  private void recoverPersonalTask(Task task) {
    task.setDeletedAt(null);
    taskRepository.save(task);
  }

  private void recoverClonedTask(Task task, ApplyScope applyScope) {
    List<Task> tasksToRecover = getClonedTasks(task, applyScope);

    for (Task taskToRecover : tasksToRecover) {
      taskToRecover.setDeletedAt(null);
    }

    taskRepository.saveAll(tasksToRecover);
  }

  private TaskType getTaskType(Task task) {
    if (task.getPlan() != null) {
      return TaskType.TEAM;
    }

    if (ruleService.isRootOrClonedTask(task)) {
      return TaskType.CLONED;
    }

    return TaskType.PERSONAL;
  }

  private List<TaskSummaryResponseDto> toTaskSummaryResponseDtoList(List<Task> tasks) {
    return tasks.stream()
        .map(
            task -> {
              TaskType taskType = getTaskType(task);
              return taskMapper.toTaskSummaryResponseDto(task, taskType);
            })
        .toList();
  }
}
