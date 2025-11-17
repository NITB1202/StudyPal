package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.common.util.Constants.PLAN_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.PlanService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
  private final PlanRepository planRepository;
  private final ModelMapper modelMapper;
  private final TaskInternalService taskService;
  private final TeamMembershipInternalService memberService;
  private final PlanHistoryInternalService historyService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public CreatePlanResponseDto createPlan(UUID userId, CreatePlanRequestDto request) {
    UUID teamId = request.getTeamId();

    memberService.validateUpdatePlanPermission(userId, teamId);

    User creator = entityManager.getReference(User.class, userId);
    Team team = entityManager.getReference(Team.class, teamId);

    String planCode = generatePlanCode(teamId);

    Plan plan =
        Plan.builder()
            .creator(creator)
            .planCode(planCode)
            .title(request.getTitle())
            .description(request.getDescription())
            .progress(0f)
            .isDeleted(false)
            .team(team)
            .build();

    planRepository.save(plan);

    taskService.createTasksForPlan(teamId, plan.getId(), request.getTasks());
    historyService.logCreatePlan(userId, plan.getId());

    return modelMapper.map(plan, CreatePlanResponseDto.class);
  }

  private String generatePlanCode(UUID teamId) {
    long planCount = planRepository.countByTeamId(teamId);
    return PLAN_CODE_PREFIX + planCount;
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

    PlanDetailResponseDto dto = modelMapper.map(plan, PlanDetailResponseDto.class);
    List<TaskResponseDto> tasks = taskService.getAll(planId);
    dto.setTasks(tasks);

    return dto;
  }

  @Override
  public ListPlanResponseDto getAssignedPlansOnDate(UUID userId, LocalDate date) {
    return null;
  }
}
