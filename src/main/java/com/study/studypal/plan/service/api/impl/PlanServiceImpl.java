package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.plan.request.CreatePersonalPlanRequestDto;
import com.study.studypal.plan.dto.plan.request.CreatePlanDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plancomment.response.PlanCommentResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.PlanService;
import com.study.studypal.plan.service.internal.PlanCommentInternalService;
import com.study.studypal.plan.service.internal.PlanRecurrenceRuleInternalService;
import com.study.studypal.plan.service.internal.PlanReminderInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
  private final PlanRepository planRepository;
  private final ModelMapper modelMapper;
  private final TaskInternalService taskService;
  private final PlanRecurrenceRuleInternalService ruleService;
  private final PlanReminderInternalService reminderService;
  private final TeamMembershipInternalService memberService;
  private final PlanCommentInternalService commentService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public CreatePlanResponseDto createPersonalPlan(
      UUID userId, CreatePersonalPlanRequestDto request) {
    CreatePlanDto planDto = request.getPlan();

    if (planDto.getStartDate().isAfter(planDto.getDueDate())) {
      throw new BaseException(PlanErrorCode.START_DATE_AFTER_DUE_DATE);
    }

    User creator = entityManager.getReference(User.class, userId);
    Plan plan = Plan.builder().creator(creator).progress(0f).isDeleted(false).build();

    modelMapper.map(planDto, plan);
    planRepository.save(plan);

    PlanInfo planInfo = modelMapper.map(plan, PlanInfo.class);

    taskService.createTasksForPersonalPlan(userId, planInfo, request.getTasks());
    ruleService.createPlanRecurrenceRule(planInfo, request.getRecurrenceRule());
    reminderService.createReminders(planInfo, request.getReminders());

    return modelMapper.map(plan, CreatePlanResponseDto.class);
  }

  @Override
  public PlanDetailResponseDto getPlanDetail(UUID userId, UUID planId) {
    Plan plan =
        planRepository
            .findById(planId)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    boolean hasPermission =
        Optional.ofNullable(plan.getTeam())
            .map(Team::getId)
            .map(teamId -> memberService.isUserInTeam(userId, teamId))
            .orElse(userId.equals(plan.getCreator().getId()));

    if (!hasPermission) {
      throw new BaseException(PlanErrorCode.PERMISSION_VIEW_PLAN_DENIED);
    }

    List<LocalDateTime> reminders = reminderService.getAll(planId);
    List<TaskResponseDto> tasks = taskService.getAll(planId);
    List<PlanCommentResponseDto> comments = commentService.getAll(planId);

    PlanDetailResponseDto dto = modelMapper.map(plan, PlanDetailResponseDto.class);
    dto.setReminders(reminders);
    dto.setTasks(tasks);
    dto.setComments(comments);

    return dto;
  }

  @Override
  public ListPlanResponseDto getAssignedPlansOnDate(UUID userId, LocalDate date) {
    return null;
  }
}
