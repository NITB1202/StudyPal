package com.study.studypal.plan.mapper;

import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.enums.TaskType;
import com.study.studypal.plan.service.internal.TaskRecurrenceRuleInternalService;
import com.study.studypal.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {
  private final ModelMapper modelMapper;
  private final TaskRecurrenceRuleInternalService ruleService;

  public List<TaskResponseDto> toTaskResponseDtoList(List<Task> tasks) {
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

  public TaskDetailResponseDto toTaskDetailResponseDto(Task task) {
    TaskDetailResponseDto responseDto = modelMapper.map(task, TaskDetailResponseDto.class);
    responseDto.setTaskType(getTaskType(task));
    return responseDto;
  }

  public TaskSummaryResponseDto toTaskSummaryResponseDto(Task task) {
    TaskSummaryResponseDto responseDto = modelMapper.map(task, TaskSummaryResponseDto.class);
    responseDto.setTaskType(getTaskType(task));
    return responseDto;
  }

  public List<TaskSummaryResponseDto> toTaskSummaryResponseDtoList(List<Task> tasks) {
    return tasks.stream().map(this::toTaskSummaryResponseDto).toList();
  }

  private TaskType getTaskType(Task task) {
    if (task.getPlan() != null) return TaskType.TEAM;
    if (ruleService.isRootOrClonedTask(task)) return TaskType.CLONED;
    return TaskType.PERSONAL;
  }
}
