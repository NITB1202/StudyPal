package com.study.studypal.plan.service.api.impl;

import static com.study.studypal.plan.constant.PlanConstant.CODE_NUMBER_FORMAT;
import static com.study.studypal.plan.constant.PlanConstant.PLAN_CODE_PREFIX;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.plan.dto.plan.internal.PlanCursor;
import com.study.studypal.plan.dto.plan.internal.PlanInfo;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.request.SearchPlanRequestDto;
import com.study.studypal.plan.dto.plan.request.UpdatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import com.study.studypal.plan.dto.plan.response.UpdatePlanResponseDto;
import com.study.studypal.plan.dto.task.response.TaskResponseDto;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.mapper.TaskMapper;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.PlanService;
import com.study.studypal.plan.service.internal.PlanHistoryInternalService;
import com.study.studypal.plan.service.internal.PlanInternalService;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.plan.service.internal.TaskNotificationService;
import com.study.studypal.plan.util.PlanCursorUtils;
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
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private final TaskNotificationService notificationService;
  private final PlanInternalService internalService;
  private final TaskMapper taskMapper;

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
            .startDate(LocalDateTime.now())
            .dueDate(LocalDateTime.now())
            .isDeleted(false)
            .team(team)
            .build();

    planRepository.save(plan);

    PlanInfo planInfo =
        PlanInfo.builder().assignerId(userId).teamId(teamId).planId(plan.getId()).build();
    taskService.createTasksForPlan(planInfo, request.getTasks());

    internalService.syncPlanFromTasks(plan);
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

    if (Boolean.TRUE.equals(plan.getIsDeleted())) {
      throw new BaseException(PlanErrorCode.PLAN_ALREADY_DELETED);
    }

    UUID teamId = plan.getTeam().getId();
    memberService.validateUserBelongsToTeam(userId, teamId);

    PlanDetailResponseDto dto = modelMapper.map(plan, PlanDetailResponseDto.class);

    List<Task> tasks = taskService.getAll(planId);
    List<TaskResponseDto> tasksDTO = taskMapper.toTaskResponseDtoList(tasks);

    int totalTasksCount = taskService.getTotalTasksCount(planId);
    int completedTasksCount = taskService.getCompletedTasksCount(planId);

    dto.setTasks(tasksDTO);
    dto.setTotalTasksCount(totalTasksCount);
    dto.setCompletedTaskCount(completedTasksCount);

    return dto;
  }

  @Override
  public List<PlanSummaryResponseDto> getPlansOnDate(UUID userId, UUID teamId, LocalDate date) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

    List<Plan> plans = planRepository.findPlansOnDate(userId, teamId, startOfDay, endOfDay);
    return modelMapper.map(plans, new TypeToken<List<PlanSummaryResponseDto>>() {}.getType());
  }

  @Override
  public ListPlanResponseDto searchPlans(UUID userId, UUID teamId, SearchPlanRequestDto request) {
    if (!request.getFromDate().isBefore(request.getToDate())) {
      throw new BaseException(CommonErrorCode.INVALID_DATE_RANGE);
    }

    memberService.validateUserBelongsToTeam(userId, teamId);
    Pageable pageable = PageRequest.of(0, request.getSize());

    List<Plan> plans;
    if (request.getCursor() != null && !request.getCursor().isEmpty()) {
      PlanCursor decodedCursor = PlanCursorUtils.decodeCursor(request.getCursor());
      plans =
          planRepository.searchPlansWithCursor(
              teamId,
              request.getKeyword(),
              request.getFromDate(),
              request.getToDate(),
              decodedCursor.dueDate(),
              decodedCursor.id(),
              pageable);
    } else {
      plans =
          planRepository.searchPlans(
              teamId, request.getKeyword(), request.getFromDate(), request.getToDate(), pageable);
    }

    List<PlanSummaryResponseDto> plansDTO =
        modelMapper.map(plans, new TypeToken<List<PlanSummaryResponseDto>>() {}.getType());
    long total = planRepository.countPlans(teamId);

    String nextCursor = null;
    if (!plans.isEmpty() && plans.size() == request.getSize()) {
      Plan lastPlan = plans.get(plans.size() - 1);
      nextCursor = PlanCursorUtils.encodeCursor(lastPlan);
    }

    return ListPlanResponseDto.builder()
        .plans(plansDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public List<String> getDatesWithPlanDueDatesInMonth(
      UUID userId, UUID teamId, Integer month, Integer year) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    LocalDate now = LocalDate.now();
    int handledMonth = month == null ? now.getMonthValue() : month;
    int handledYear = year == null ? now.getYear() : year;

    if (handledMonth < 1 || handledMonth > 12) {
      throw new BaseException(CommonErrorCode.INVALID_M0NTH);
    }

    if (handledYear <= 0) {
      throw new BaseException(CommonErrorCode.INVALID_YEAR);
    }

    List<LocalDateTime> dueDates =
        planRepository.findPlanDueDatesByTeamIdInMonth(teamId, handledMonth, handledYear);

    return dueDates.stream().map(d -> d.toLocalDate().toString()).distinct().sorted().toList();
  }

  @Override
  public UpdatePlanResponseDto updatePlan(UUID userId, UUID planId, UpdatePlanRequestDto request) {
    Plan plan =
        planRepository
            .findByIdForUpdate(planId)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    if (Boolean.TRUE.equals(plan.getIsDeleted())) {
      throw new BaseException(PlanErrorCode.PLAN_ALREADY_DELETED);
    }

    memberService.validateUpdatePlanPermission(userId, plan.getTeam().getId());

    if (request.getTitle() != null && request.getTitle().isBlank()) {
      throw new BaseException(CommonErrorCode.FIELD_BLANK, "Title");
    }

    modelMapper.map(request, plan);
    planRepository.save(plan);

    historyService.logUpdatePlan(userId, planId);
    notificationService.publishPlanUpdatedNotification(userId, plan);

    return modelMapper.map(plan, UpdatePlanResponseDto.class);
  }

  @Override
  public ActionResponseDto deletePlan(UUID userId, UUID planId) {
    Plan plan =
        planRepository
            .findByIdForUpdate(planId)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    if (Boolean.TRUE.equals(plan.getIsDeleted())) {
      throw new BaseException(PlanErrorCode.PLAN_ALREADY_DELETED);
    }

    memberService.validateUpdatePlanPermission(userId, plan.getTeam().getId());
    internalService.softDeletePlan(plan);

    Set<UUID> relatedMemberIds = internalService.getPlanRelatedMemberIds(planId);

    taskService.deleteAllTasksByPlanId(planId);
    historyService.logDeletePlan(userId, planId);
    notificationService.publishPlanDeletedNotification(userId, plan, relatedMemberIds);

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }
}
