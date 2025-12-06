package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.plan.request.SearchPlanRequestDto;
import com.study.studypal.plan.dto.plan.request.UpdatePlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import com.study.studypal.plan.dto.plan.response.ListPlanResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanDetailResponseDto;
import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import com.study.studypal.plan.dto.plan.response.UpdatePlanResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PlanService {
  CreatePlanResponseDto createPlan(UUID userId, CreatePlanRequestDto request);

  PlanDetailResponseDto getPlanDetail(UUID userId, UUID planId);

  List<PlanSummaryResponseDto> getPlansOnDate(UUID userId, UUID teamId, LocalDate date);

  ListPlanResponseDto searchPlans(UUID userId, UUID teamId, SearchPlanRequestDto request);

  List<String> getDatesWithPlanDueDatesInMonth(
      UUID userId, UUID teamId, Integer month, Integer year);

  UpdatePlanResponseDto updatePlan(UUID userId, UUID planId, UpdatePlanRequestDto request);

  ActionResponseDto deletePlan(UUID userId, UUID planId);
}
