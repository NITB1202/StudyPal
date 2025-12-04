package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TaskRecurrenceRule;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRecurrenceRuleRepository extends JpaRepository<TaskRecurrenceRule, UUID> {
  Optional<TaskRecurrenceRule> findByTaskId(UUID taskId);

  boolean existsByTaskId(UUID taskId);
}
