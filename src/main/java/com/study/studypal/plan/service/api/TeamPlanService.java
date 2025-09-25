package com.study.studypal.plan.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.plan.dto.plan.request.CreateTeamPlanRequestDto;
import java.util.UUID;

public interface TeamPlanService {
  ActionResponseDto createTeamPlan(UUID userId, CreateTeamPlanRequestDto request);
}
