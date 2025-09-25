package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Task;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<UUID, Task> {}
