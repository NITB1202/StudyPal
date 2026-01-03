package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.enums.ContextType;
import com.study.studypal.chatbot.exception.ChatMessageContextErrorCode;
import com.study.studypal.chatbot.mapper.ChatMessageContextMapper;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.JsonUtils;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.exception.TaskErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.repository.TaskRepository;
import com.study.studypal.plan.service.internal.TaskValidationService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageContextServiceImpl implements ChatMessageContextService {
  private final TaskRepository taskRepository;
  private final PlanRepository planRepository;
  private final ChatMessageContextMapper mapper;
  private final TeamMembershipInternalService memberService;
  private final TaskValidationService validationService;

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
  public String validateAndSerializeContext(UUID userId, UUID contextId, ContextType contextType) {
    if (contextId == null) return "";
    if (contextType == null)
      throw new BaseException(ChatMessageContextErrorCode.CONTEXT_TYPE_REQUIRED);

    Object context =
        switch (contextType) {
          case PLAN -> {
            Plan plan = getPlanById(contextId);
            validatePlanAccess(userId, plan);
            yield mapper.toPlanContext(plan);
          }
          case TASK -> {
            Task task = getTaskById(contextId);
            validateTaskAccess(userId, task);
            yield mapper.toTaskContext(task);
          }
        };

    return JsonUtils.trySerialize(context).orElse("");
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

  private void validateTaskAccess(UUID userId, Task task) {
    validationService.validateViewTaskPermission(userId, task);
  }

  private void validatePlanAccess(UUID userId, Plan plan) {
    UUID teamId = plan.getTeam().getId();
    memberService.validateUserBelongsToTeam(userId, teamId);
  }
}
