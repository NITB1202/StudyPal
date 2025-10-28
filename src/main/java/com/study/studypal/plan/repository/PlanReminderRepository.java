package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanReminder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanReminderRepository extends JpaRepository<PlanReminder, UUID> {
  List<PlanReminder> findAllByPlanIdOrderByRemindAtAsc(UUID planId);
}
