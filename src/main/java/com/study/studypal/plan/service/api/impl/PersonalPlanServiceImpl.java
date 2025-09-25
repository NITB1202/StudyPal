package com.study.studypal.plan.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.plan.request.CreatePersonalPlanRequestDto;
import com.study.studypal.plan.dto.plan.request.CreatePlanDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.task.internal.ValidateTasksInfo;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.api.PersonalPlanService;
import com.study.studypal.plan.service.internal.PlanRecurrenceRuleInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalPlanServiceImpl implements PersonalPlanService {
  private final PlanRepository planRepository;
  private final ModelMapper modelMapper;
  private final TaskInternalService taskService;
  private final PlanRecurrenceRuleInternalService ruleService;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public ActionResponseDto createPersonalPlan(UUID userId, CreatePersonalPlanRequestDto request) {
    CreatePlanDto planDto = request.getPlan();

    if (planDto.getStartDate().isAfter(planDto.getDueDate())) {
      throw new BaseException(PlanErrorCode.START_DATE_AFTER_DUE_DATE);
    }

    User creator = entityManager.getReference(User.class, userId);
    Plan plan = Plan.builder().creator(creator).progress(0f).isDeleted(false).build();

    modelMapper.map(planDto, plan);
    planRepository.save(plan);

    ValidateTasksInfo planInfo =
        new ValidateTasksInfo(plan.getId(), plan.getStartDate(), plan.getDueDate());
    taskService.createTasksForPersonalPlan(userId, planInfo, request.getTasks());
    ruleService.createPlanRecurrenceRule(plan.getId(), request.getRecurrenceRule());

    return ActionResponseDto.builder().success(true).message("Create successfully.").build();
  }

  @Override
  public PlanDetailResponseDto getPlanDetail(UUID planId) {
    return null;
  }

  @Override
  public ListPlanResponseDto getAssignedPlansOnDate(UUID userId, LocalDate date) {
    return null;
  }
}
