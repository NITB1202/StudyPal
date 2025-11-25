package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanHistoryRepository extends JpaRepository<PlanHistory, UUID> {}
