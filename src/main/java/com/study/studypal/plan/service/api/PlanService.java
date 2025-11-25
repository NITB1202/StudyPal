package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import java.time.LocalDate;
import java.util.UUID;

public interface PlanService {
  CreatePlanResponseDto createPlan(UUID userId, CreatePlanRequestDto request);

  PlanDetailResponseDto getPlanDetail(UUID userId, UUID planId);

  ListPlanResponseDto getAssignedPlansOnDate(UUID userId, LocalDate date);
}
