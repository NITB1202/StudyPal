package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findAllByPlanIdOrderByDueDateAsc(UUID planId);

  List<Task> findAllByPlanId(UUID planId);

  int countByPlanId(UUID planId);

  int countByPlanIdAndCompleteDateIsNotNull(UUID planId);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.assignee.id = :userId
      AND t.startDate <= :endOfDay
      AND t.dueDate >= :startOfDay
    ORDER BY t.dueDate ASC,
             CASE t.priority
                 WHEN 'HIGH' THEN 1
                 WHEN 'MEDIUM' THEN 2
                 WHEN 'LOW' THEN 3
             END ASC
    """)
  List<Task> getAssignedTasksOnDate(
      @Param("userId") UUID userId,
      @Param("startOfDay") LocalDateTime startOfDay,
      @Param("endOfDay") LocalDateTime endOfDay);

  @Query(
      """
        SELECT t
        FROM Task t
        WHERE t.assignee.id = :userId
          AND MONTH(t.dueDate) = :month
          AND YEAR(t.dueDate) = :year
    """)
  List<Task> findTasksByAssigneeAndDueDateInMonth(
      @Param("userId") UUID userId, @Param("month") Integer month, @Param("year") Integer year);
}
