package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.task.request.CreateTaskForPersonalPlanDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskInternalServiceImpl implements TaskInternalService {
  private final TaskRepository taskRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createTasksForPersonalPlan(
      UUID userId, PlanInfo planInfo, List<CreateTaskForPersonalPlanDto> tasks) {
    List<Task> savedTasks = new ArrayList<>();
    Set<String> contents = new HashSet<>();

    Plan plan = entityManager.getReference(Plan.class, planInfo.getPlanId());
    User user = entityManager.getReference(User.class, userId);

    for (CreateTaskForPersonalPlanDto taskDto : tasks) {
      String content = taskDto.getContent();
      LocalDateTime dueDate = taskDto.getDueDate();

      if (contents.contains(content)) {
        throw new BaseException(TaskErrorCode.TASK_ALREADY_EXISTS, content);
      } else {
        contents.add(content);
      }

      if (dueDate.isBefore(planInfo.getPlanStartDate())
          || dueDate.isAfter(planInfo.getPlanDueDate())) {
        throw new BaseException(TaskErrorCode.INVALID_DUE_DATE, content);
      }

      Task task =
          Task.builder()
              .plan(plan)
              .assignee(user)
              .content(content)
              .dueDate(taskDto.getDueDate())
              .build();

      savedTasks.add(task);
    }

    taskRepository.saveAll(savedTasks);
  }
}
