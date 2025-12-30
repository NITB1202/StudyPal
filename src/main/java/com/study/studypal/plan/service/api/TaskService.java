package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import com.study.studypal.plan.dto.task.request.SearchTasksRequestDto;
import com.study.studypal.plan.dto.task.request.UpdateTaskRequestDto;
import com.study.studypal.plan.dto.task.response.CreateTaskResponseDto;
import com.study.studypal.plan.dto.task.response.ListDeletedTaskResponseDto;
import com.study.studypal.plan.dto.task.response.ListTaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.UpdateTaskResponseDto;
import com.study.studypal.plan.enums.ApplyScope;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskService {
  CreateTaskResponseDto createTask(UUID userId, CreateTaskRequestDto request);

  TaskDetailResponseDto getTaskDetail(UUID userId, UUID taskId);

  List<TaskSummaryResponseDto> getAssignedTasksOnDate(UUID userId, LocalDate date);

  List<String> getDatesWithTaskDueDateInMonth(UUID userId, Integer month, Integer year);

  ListDeletedTaskResponseDto getDeletedTasks(
      UUID userId, UUID teamId, LocalDateTime cursor, int size);

  ListTaskResponseDto searchTasks(UUID userId, SearchTasksRequestDto request);

  UpdateTaskResponseDto updateTask(
      UUID userId, UUID taskId, ApplyScope applyScope, UpdateTaskRequestDto request);

  ActionResponseDto markTaskAsCompleted(UUID userId, UUID taskId);

  ActionResponseDto deleteTask(UUID userId, UUID taskId, ApplyScope applyScope);

  ActionResponseDto recoverTask(UUID userId, UUID taskId, ApplyScope applyScope);

  ActionResponseDto permanentlyDeleteTask(UUID userId, UUID taskId, ApplyScope applyScope);
}
