package com.study.studypal.plan.service.internal.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.TASK_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.internal.TaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.service.internal.TaskReminderService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private final TaskReminderService reminderService;
  private final TaskCounterService taskCounterService;
  private final TaskNotificationService notificationService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createTasksForPlan(PlanInfo planInfo, List<CreateTaskForPlanRequestDto> tasks) {
    Plan plan = entityManager.getReference(Plan.class, planInfo.getPlanId());

    for (CreateTaskForPlanRequestDto taskDto : tasks) {
      UUID assigneeId = taskDto.getAssigneeId();
      LocalDateTime startDate = taskDto.getStartDate();
      LocalDateTime dueDate = taskDto.getDueDate();

      if (dueDate.isBefore(startDate)) {
        throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, taskDto.getContent());
      }

      memberService.validateUserBelongsToTeam(assigneeId, planInfo.getTeamId());

      User assignee = entityManager.getReference(User.class, assigneeId);
      String taskCode = generateTaskCode(planInfo.getTeamId());

      Task task =
          Task.builder()
              .taskCode(taskCode)
              .content(taskDto.getContent())
              .startDate(taskDto.getStartDate())
              .dueDate(taskDto.getDueDate())
              .note(taskDto.getNote())
              .priority(taskDto.getPriority())
              .plan(plan)
              .assignee(assignee)
              .build();

      taskRepository.save(task);

      TaskInfo taskInfo = modelMapper.map(task, TaskInfo.class);
      reminderService.createReminders(taskInfo, taskDto.getReminders());
      notificationService.publishTaskAssignedNotification(planInfo.getAssignerId(), task);
    }
  }

  @Override
  public Task getById(UUID id) {
    return taskRepository
        .findById(id)
        .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));
  }

  private String generateTaskCode(UUID teamId) {
    return TASK_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseTeamTaskCounter(teamId));
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

      responseDtoList.add(responseDto);
    }

    return responseDtoList;
  }
}
