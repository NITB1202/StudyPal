package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TaskRecurrenceRule;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRecurrenceRuleRepository extends JpaRepository<TaskRecurrenceRule, UUID> {}
