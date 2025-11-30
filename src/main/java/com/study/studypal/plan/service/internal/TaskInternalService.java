package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.internal.CreateTaskInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPlanRequestDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.modelmapper.internal.Pair;

public interface TaskInternalService {
  void createTasksForPlan(PlanInfo planInfo, List<CreateTaskForPlanRequestDto> tasks);

  Task createTask(UUID assigneeId, Pair<UUID, UUID> planInfo, CreateTaskInfo taskInfo);

  void cloneTask(UUID rootTaskId, List<LocalDate> recurrenceDates);

  Task getById(UUID id);

  List<TaskResponseDto> getAll(UUID planId);

  int getTotalTasksCount(UUID planId);

  int getCompletedTasksCount(UUID planId);

  Pair<LocalDateTime, LocalDateTime> getPlanPeriod(UUID planId);

  void validateViewTaskPermission(UUID userId, Task task);

  void validateUpdateTaskPermission(UUID userId, Task task);

  void validateTaskOwnership(UUID userId, Task task);

  void validatePersonalTask(Task task);

  void validateTeamTask(Task task);
}
