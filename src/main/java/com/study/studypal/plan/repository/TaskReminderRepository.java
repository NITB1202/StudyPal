package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TaskReminder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskReminderRepository extends JpaRepository<TaskReminder, UUID> {}
