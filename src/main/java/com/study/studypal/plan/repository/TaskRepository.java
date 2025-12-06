package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Task;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findAllByPlanId(UUID planId);

  @Query(
      """
   SELECT t
   FROM Task t
   WHERE t.plan.id = :planId
   AND t.deletedAt IS NULL
   ORDER BY t.dueDate ASC, t.startDate ASC
   """)
  List<Task> findAllByPlanIdOrderByDates(@Param("planId") UUID planId);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    WHERE t.plan.id = :planId
    AND t.deletedAt IS NULL
    """)
  int countTasks(UUID planId);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    WHERE t.plan.id = :planId
    AND t.deletedAt IS NULL
    AND t.completeDate IS NOT NULL
    """)
  int countCompletedTasks(UUID planId);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.assignee.id = :userId
    AND t.startDate <= :endOfDay
    AND t.dueDate >= :startOfDay
    AND t.deletedAt IS NULL
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
    SELECT t.dueDate
    FROM Task t
    WHERE t.assignee.id = :userId
    AND MONTH(t.dueDate) = :month
    AND YEAR(t.dueDate) = :year
    AND t.deletedAt IS NULL
    """)
  List<LocalDateTime> findTaskDueDatesByUserIdInMonth(
      @Param("userId") UUID userId, @Param("month") Integer month, @Param("year") Integer year);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.parentTask.id = :taskId
    AND t.deletedAt IS NULL
    AND t.dueDate >= CURRENT_TIMESTAMP
    ORDER BY t.dueDate ASC
    """)
  List<Task> findAllActiveChildTasks(UUID taskId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT t FROM Task t WHERE t.id = :taskId")
  Optional<Task> findByIdForUpdate(@Param("taskId") UUID taskId);

  @Query(
      """
    SELECT DISTINCT a.id
    FROM Task t
    JOIN t.assignee a
    WHERE t.plan.id = :planId
      AND t.deletedAt IS NULL
    """)
  Set<UUID> findDistinctAssigneeIdsByPlan(@Param("planId") UUID planId);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.assignee a
    WHERE a.id = :userId
    AND t.plan IS NULL
    AND t.deletedAt IS NOT NULL
    ORDER BY t.deletedAt DESC
    """)
  List<Task> getPersonalDeletedTasks(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.assignee a
    WHERE a.id = :userId
    AND t.plan IS NULL
    AND t.deletedAt <= :cursor
    ORDER BY t.deletedAt DESC
    """)
  List<Task> getPersonalDeletedTasksWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt IS NOT NULL
    ORDER BY t.deletedAt DESC
    """)
  List<Task> getTeamDeletedTasks(@Param("teamId") UUID teamId, Pageable pageable);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt <= :cursor
    ORDER BY t.deletedAt DESC
    """)
  List<Task> getTeamDeletedTasksWithCursor(
      @Param("teamId") UUID teamId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt IS NOT NULL
    """)
  long countTeamDeletedTasks(@Param("teamId") UUID teamId);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    JOIN t.assignee a
    WHERE a.id = :userId
    AND t.plan IS NULL
    AND t.deletedAt IS NOT NULL
    """)
  long countPersonalDeletedTasks(@Param("userId") UUID userId);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.parentTask.id = :taskId
    AND t.deletedAt IS NOT NULL
    """)
  List<Task> findAllDeletedChildTask(@Param("taskId") UUID taskId);
}
