package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Task;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findAllByPlanIdOrderByDueDateAsc(UUID planId);

  int countByPlanId(UUID planId);

  int countByPlanIdAndCompleteDateIsNotNull(UUID planId);
}
