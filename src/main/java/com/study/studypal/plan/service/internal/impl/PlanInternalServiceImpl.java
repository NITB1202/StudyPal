package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.entity.Plan;
import com.study.studypal.plan.exception.PlanErrorCode;
import com.study.studypal.plan.repository.PlanRepository;
import com.study.studypal.plan.service.internal.PlanInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanInternalServiceImpl implements PlanInternalService {
  private final PlanRepository planRepository;

  @Override
  public UUID getTeamIdById(UUID id) {
    Plan plan =
        planRepository
            .findByIdWithTeam(id)
            .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

    return plan.getTeam().getId();
  }
}
