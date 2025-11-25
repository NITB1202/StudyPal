package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.PLAN_CODE_PREFIX;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.DateErrorCode;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.PlanService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Pair;
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
  private final TaskCounterService taskCounterService;

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

    PlanInfo planInfo =
        PlanInfo.builder().assignerId(userId).teamId(teamId).planId(plan.getId()).build();

    taskService.createTasksForPlan(planInfo, request.getTasks());
    historyService.logCreatePlan(userId, plan.getId());

    return modelMapper.map(plan, CreatePlanResponseDto.class);
  }

  private String generatePlanCode(UUID teamId) {
    return PLAN_CODE_PREFIX
        + String.format(CODE_NUMBER_FORMAT, taskCounterService.increaseTeamTaskCounter(teamId));
  }

  @Override
  public PlanDetailResponseDto getPlanDetail(UUID userId, UUID planId) {
    Plan plan =
        planRepository
            .findById(planId)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    UUID teamId = plan.getTeam().getId();
    memberService.validateUserBelongsToTeam(userId, teamId);

    PlanDetailResponseDto dto = modelMapper.map(plan, PlanDetailResponseDto.class);

    List<TaskResponseDto> tasks = taskService.getAll(planId);
    int totalTasksCount = taskService.getTotalTasksCount(planId);
    int completedTasksCount = taskService.getCompletedTasksCount(planId);
    Pair<LocalDateTime, LocalDateTime> planPeriod = taskService.getPlanPeriod(planId);

    dto.setTasks(tasks);
    dto.setTotalTasksCount(totalTasksCount);
    dto.setCompletedTaskCount(completedTasksCount);
    dto.setStartDate(planPeriod.getLeft());
    dto.setDueDate(planPeriod.getRight());

    return dto;
  }

  @Override
  public List<PlanSummaryResponseDto> getPlansOnDate(UUID userId, UUID teamId, LocalDate date) {
    memberService.validateUserBelongsToTeam(userId, teamId);
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
    return planRepository.findPlansOnDate(userId, teamId, startOfDay, endOfDay);
  }

  @Override
  public List<String> getDatesWithPlanDueDatesInMonth(
      UUID userId, UUID teamId, Integer month, Integer year) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    LocalDate now = LocalDate.now();
    int handledMonth = month == null ? now.getMonthValue() : month;
    int handledYear = year == null ? now.getYear() : year;

    if (handledMonth < 1 || handledMonth > 12) {
      throw new BaseException(DateErrorCode.INVALID_M0NTH);
    }

    if (handledYear <= 0) {
      throw new BaseException(DateErrorCode.INVALID_YEAR);
    }

    List<LocalDate> dueDates =
        planRepository.findPlanDueDatesInMonthByTeam(teamId, handledMonth, handledYear);

    return dueDates.stream().map(LocalDate::toString).distinct().sorted().toList();
  }
}
