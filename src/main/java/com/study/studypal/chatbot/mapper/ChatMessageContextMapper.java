package com.study.studypal.chatbot.mapper;

import com.study.studypal.chatbot.dto.internal.PlanContext;
import com.study.studypal.chatbot.dto.internal.TaskContext;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageContextMapper {
  private final ModelMapper modelMapper;

  public TaskContext toTaskContext(Task task) {
    TaskContext context = modelMapper.map(task, TaskContext.class);
    context.setAssigneeName(task.getAssignee().getName());
    return context;
  }

  public PlanContext toPlanContext(Plan plan) {
    PlanContext context = modelMapper.map(plan, PlanContext.class);
    List<TaskContext> tasks = plan.getTasks().stream().map(this::toTaskContext).toList();
    context.setTasks(tasks);
    return context;
  }
}
