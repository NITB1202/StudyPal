package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanHistoryRepository extends JpaRepository<PlanHistory, UUID> {}
