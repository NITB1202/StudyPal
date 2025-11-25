package com.study.studypal.plan.service.api;

import com.study.studypal.plan.dto.history.ListPlanHistoryResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PlanHistoryService {
  ListPlanHistoryResponseDto getPlanHistory(
      UUID userId, UUID planId, LocalDateTime cursor, int size);
}
