package com.study.studypal.plan.mapper;

import com.study.studypal.plan.dto.task.response.TaskDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.dto.task.response.TaskSummaryResponseDto;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.enums.TaskType;
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

  public TaskDetailResponseDto toTaskDetailResponseDto(Task task, TaskType taskType) {
    TaskDetailResponseDto responseDto = modelMapper.map(task, TaskDetailResponseDto.class);
    responseDto.setTaskType(taskType);
    return responseDto;
  }

  public TaskSummaryResponseDto toTaskSummaryResponseDto(Task task, TaskType taskType) {
    TaskSummaryResponseDto responseDto = modelMapper.map(task, TaskSummaryResponseDto.class);
    responseDto.setTaskType(taskType);
    return responseDto;
  }
}
