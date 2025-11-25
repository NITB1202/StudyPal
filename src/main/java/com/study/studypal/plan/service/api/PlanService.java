package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PlanService {
  CreatePlanResponseDto createPlan(UUID userId, CreatePlanRequestDto request);

  PlanDetailResponseDto getPlanDetail(UUID userId, UUID planId);

  List<PlanSummaryResponseDto> getPlansOnDate(UUID userId, UUID teamId, LocalDate date);

  List<String> getDatesWithPlanDueDatesInMonth(
      UUID userId, UUID teamId, Integer month, Integer year);
}
