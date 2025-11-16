package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.plan.request.CreatePlanDto;
import com.study.studypal.plan.dto.plan.request.CreateTeamPlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.TeamPlanService;
import com.study.studypal.plan.service.internal.PlanReminderInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamPlanServiceImpl implements TeamPlanService {
  private final PlanRepository planRepository;
  private final ModelMapper modelMapper;
  private final TeamMembershipInternalService memberService;
  private final TaskInternalService taskService;
  private final PlanReminderInternalService reminderService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public CreatePlanResponseDto createTeamPlan(UUID userId, CreateTeamPlanRequestDto request) {
    UUID teamId = request.getTeamId();
    CreatePlanDto planDto = request.getPlan();

    if (planDto.getStartDate().isAfter(planDto.getDueDate())) {
      throw new BaseException(PlanErrorCode.START_DATE_AFTER_DUE_DATE);
    }

    memberService.validateUpdatePlanPermission(userId, teamId);

    User creator = entityManager.getReference(User.class, userId);
    Team team = entityManager.getReference(Team.class, teamId);
    Plan plan = Plan.builder().creator(creator).team(team).progress(0f).isDeleted(false).build();

    modelMapper.map(planDto, plan);
    planRepository.save(plan);

    PlanInfo planInfo = modelMapper.map(plan, PlanInfo.class);

    taskService.createTasksForTeamPlan(teamId, planInfo, request.getTasks());
    reminderService.createReminders(planInfo, request.getReminders());

    return modelMapper.map(plan, CreatePlanResponseDto.class);
  }
}
