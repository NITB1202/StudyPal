package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanReminder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanReminderRepository extends JpaRepository<UUID, PlanReminder> {}
