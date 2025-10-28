package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Task;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findAllByPlanIdOrderByDueDateAsc(UUID planId);
}
