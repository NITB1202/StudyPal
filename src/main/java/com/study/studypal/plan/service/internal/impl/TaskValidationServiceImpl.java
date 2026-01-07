package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.task.internal.UpdateTaskInfo;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.service.internal.TaskValidationService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskValidationServiceImpl implements TaskValidationService {
  private final TeamMembershipInternalService memberService;

  @Override
  public void validateViewTaskPermission(UUID userId, Task task) {
    Plan plan = task.getPlan();
    if (plan != null) {
      validateUserBelongsToTeam(userId, plan);
    } else {
      validateTaskOwnership(userId, task);
    }
  }

  @Override
  public void validateTaskOwnership(UUID userId, Task task) {
    User assignee = task.getAssignee();
    if (!userId.equals(assignee.getId())) {
      throw new BaseException(TaskErrorCode.PERMISSION_TASK_OWNER_DENIED);
    }
  }

  @Override
  public void validatePersonalTask(Task task) {
    if (task.getPlan() != null) {
      throw new BaseException(TaskErrorCode.PERSONAL_TASK_REQUIRED);
    }
  }

  @Override
  public void validateTeamTask(Task task) {
    if (task.getPlan() == null) {
      throw new BaseException(TaskErrorCode.TEAM_TASK_REQUIRED);
    }
  }

  @Override
  public void validateUpdateTaskRequest(Task task, UpdateTaskInfo info) {
    if (info.getContent() != null && info.getContent().isBlank()) {
      throw new BaseException(CommonErrorCode.FIELD_BLANK, "Content");
    }

    LocalDateTime startDate =
        info.getStartDate() != null ? info.getStartDate() : task.getStartDate();
    LocalDateTime dueDate = info.getDueDate() != null ? info.getDueDate() : task.getDueDate();

    if (dueDate.isBefore(startDate)) {
      throw new BaseException(CommonErrorCode.INVALID_TIME_RANGE);
    }
  }

  @Override
  public void validateTaskDeleted(Task task) {
    if (task.getDeletedAt() == null) {
      throw new BaseException(TaskErrorCode.TASK_NOT_DELETED);
    }
  }

  @Override
  public void validateTaskNotDeleted(Task task) {
    if (task.getDeletedAt() != null) {
      throw new BaseException(TaskErrorCode.TASK_ALREADY_DELETED);
    }
  }

  @Override
  public void validateTaskIsIncomplete(Task task) {
    if (task.getCompletedAt() != null) {
      throw new BaseException(TaskErrorCode.TASK_ALREADY_COMPLETED);
    }
  }

  @Override
  public void validateUpdateTaskPermission(UUID userId, Task task) {
    Plan plan = task.getPlan();
    if (plan != null) {
      validateUpdateTaskPermission(userId, plan);
    } else {
      validateTaskOwnership(userId, task);
    }
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
