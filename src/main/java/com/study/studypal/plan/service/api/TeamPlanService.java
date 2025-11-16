package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.plan.request.CreateTeamPlanRequestDto;
import com.study.studypal.plan.dto.plan.response.CreatePlanResponseDto;
import java.util.UUID;

public interface TeamPlanService {
  CreatePlanResponseDto createTeamPlan(UUID userId, CreateTeamPlanRequestDto request);
}
