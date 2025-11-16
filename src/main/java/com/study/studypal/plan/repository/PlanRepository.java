package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Plan;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
  long countByTeamId(UUID teamId);
}
