package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TaskReminder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskReminderRepository extends JpaRepository<TaskReminder, UUID> {
  List<TaskReminder> findAllByTaskIdOrderByRemindAtAsc(UUID taskId);
}
