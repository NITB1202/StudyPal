package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanRecurrenceRule;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRecurrenceRuleRepository extends JpaRepository<PlanRecurrenceRule, UUID> {}
