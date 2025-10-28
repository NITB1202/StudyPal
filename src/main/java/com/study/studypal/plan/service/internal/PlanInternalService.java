package com.study.studypal.plan.service.internal;

import com.study.studypal.plan.entity.Plan;
import java.util.UUID;

public interface PlanInternalService {
  Plan getById(UUID planId);
}
