package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.plan.request.CreatePersonalPlanRequestDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PersonalPlanDetailResponseDto;
import java.time.LocalDate;
import java.util.UUID;

public interface PlanService {
  ActionResponseDto createPersonalPlan(UUID userId, CreatePersonalPlanRequestDto request);

  PersonalPlanDetailResponseDto getPersonalPlanDetail(UUID planId);

  ListPlanResponseDto getAssignedPlansOnDate(UUID userId, LocalDate date);
}
