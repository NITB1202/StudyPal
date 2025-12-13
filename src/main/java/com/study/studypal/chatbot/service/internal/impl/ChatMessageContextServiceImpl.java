package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.enums.ContextType;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.repository.TaskRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageContextServiceImpl implements ChatMessageContextService {
  private final TaskRepository taskRepository;
  private final PlanRepository planRepository;

  @Override
  public String getContextCode(UUID contextId, ContextType contextType) {
    if (contextId == null) return null;

    return switch (contextType) {
      case PLAN -> getPlanById(contextId).getPlanCode();
      case TASK -> getTaskById(contextId).getTaskCode();
    };
  }

  @Override
  public String validateAndSerializeContext(UUID contextId, ContextType contextType) {
    return "";
  }

  private Plan getPlanById(UUID planId) {
    return planRepository
        .findById(planId)
        .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));
  }

  private Task getTaskById(UUID taskId) {
    return taskRepository
        .findById(taskId)
        .orElseThrow(() -> new BaseException(TaskErrorCode.TASK_NOT_FOUND));
  }
}
