package com.study.studypal.chatbot.service.internal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.studypal.chatbot.dto.internal.PlanContext;
import com.study.studypal.chatbot.dto.internal.TaskContext;
import com.study.studypal.chatbot.enums.ContextType;
import com.study.studypal.chatbot.exception.ChatMessageContextErrorCode;
import com.study.studypal.chatbot.mapper.ChatMessageContextMapper;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageContextServiceImpl implements ChatMessageContextService {
  private final TaskRepository taskRepository;
  private final PlanRepository planRepository;
  private final ObjectMapper objectMapper;
  private final ChatMessageContextMapper mapper;

  @Override
  public String getContextCode(UUID contextId, ContextType contextType) {
    if (contextId == null) return null;

    return switch (contextType) {
      case PLAN -> getPlanById(contextId).getPlanCode();
      case TASK -> getTaskById(contextId).getTaskCode();
    };
  }

  @Override
  @Transactional
  public String validateAndSerializeContext(UUID contextId, ContextType contextType) {
    if (contextId == null) return "";
    if (contextType == null)
      throw new BaseException(ChatMessageContextErrorCode.CONTEXT_TYPE_REQUIRED);

    Object context =
        switch (contextType) {
          case PLAN -> getPlanContext(contextId);
          case TASK -> getTaskContext(contextId);
        };

    try {
      return objectMapper.writeValueAsString(context);
    } catch (Exception ex) {
      throw new BaseException(ChatMessageContextErrorCode.SERIALIZE_CONTEXT_FAILED);
    }
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

  private TaskContext getTaskContext(UUID taskId) {
    Task task = getTaskById(taskId);
    return mapper.toTaskContext(task);
  }

  private PlanContext getPlanContext(UUID planId) {
    Plan plan = getPlanById(planId);
    return mapper.toPlanContext(plan);
  }
}
