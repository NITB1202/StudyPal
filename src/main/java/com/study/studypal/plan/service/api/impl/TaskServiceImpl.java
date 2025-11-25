package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskAdditionalDataResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.api.TaskService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskReminderInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
  private final TaskReminderInternalService reminderService;

  @Override
  @Transactional
  public CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request) {
    CreateTaskInfo createTaskInfo = modelMapper.map(request, CreateTaskInfo.class);
    Task task = internalService.createTask(userId, Pair.of(null, null), createTaskInfo);
    reminderService.scheduleReminder(task.getDueDate(), task);
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
    LocalDateTime startOfDate = date.atStartOfDay();
    LocalDateTime endOfDate = date.atTime(LocalTime.MAX);

    List<Task> tasks = taskRepository.getAssignedTasksOnDate(userId, startOfDate, endOfDate);

    return tasks.stream()
        .map(
            t -> {
              TaskSummaryResponseDto summary = modelMapper.map(t, TaskSummaryResponseDto.class);
              summary.setCompleted(t.getCompleteDate() != null);
              summary.setCopy(t.getParentTask() != null);
              return summary;
            })
        .toList();
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
}
