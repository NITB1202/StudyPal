package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.TASK_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
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
import java.util.Set;
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
      throw new BaseException(CommonErrorCode.INVALID_DATE_RANGE);
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
  public List<Task> getAll(UUID planId) {
    return taskRepository.findAllByPlanIdOrderByDates(planId);
  }

  @Override
  public int getTotalTasksCount(UUID planId) {
    return taskRepository.countTasks(planId);
  }

  @Override
  public int getCompletedTasksCount(UUID planId) {
    return taskRepository.countCompletedTasks(planId);
  }

  @Override
  public Set<UUID> getDistinctAssigneeIdsByPlanId(UUID planId) {
    return taskRepository.findDistinctAssigneeIdsByPlan(planId);
  }

  @Override
  public List<Task> getAllActiveClonedTasksIncludingOriginal(Task task) {
    Task rootTask = task.getParentTask() != null ? task.getParentTask() : task;
    List<Task> tasks = taskRepository.findAllActiveChildTasks(rootTask.getId());
    tasks.add(0, rootTask);
    return tasks;
  }

  @Override
  public void deleteAllTasksByPlanId(UUID planId) {
    List<Task> tasks = taskRepository.findAllByPlanId(planId);

    for (Task task : tasks) {
      reminderService.deleteAllRemindersForTask(task.getId());
      task.setDeletedAt(LocalDateTime.now());
    }

    taskRepository.saveAll(tasks);
  }

  @Override
  public void detachFromParent(Task task) {
    task.setParentTask(null);
    taskRepository.save(task);
  }

  @Override
  public void hardDelete(List<Task> tasks) {
    for (Task task : tasks) {
      reminderService.deleteAllRemindersForTask(task.getId());
    }

    taskRepository.deleteAll(tasks);
  }
}
