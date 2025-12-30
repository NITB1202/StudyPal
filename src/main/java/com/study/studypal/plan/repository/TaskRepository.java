package com.study.studypal.plan.repository;

import com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto;
import com.study.studypal.plan.dto.task.internal.TaskCursor;
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
    AND t.completedAt IS NOT NULL
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
    ORDER BY t.dueDate ASC, t.priorityValue ASC
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
  List<Task> findAllDeletedChildTasks(@Param("taskId") UUID taskId);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.deletedAt IS NULL
      AND t.assignee.id = :userId
      AND (
           LOWER(t.content) LIKE CONCAT('%', :keyword, '%') OR
           LOWER(t.taskCode) LIKE CONCAT('%', :keyword, '%')
      )
      AND t.dueDate >= :fromDate
      AND t.startDate <= :toDate
    ORDER BY
        t.dueDate ASC,
        t.priorityValue ASC,
        t.id ASC
    """)
  List<Task> searchTasks(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.deletedAt IS NULL
      AND t.assignee.id = :userId
      AND (
           LOWER(t.content) LIKE CONCAT('%', :keyword, '%')
           OR LOWER(t.taskCode) LIKE CONCAT('%', :keyword, '%')
      )
      AND t.dueDate >= :fromDate
      AND t.startDate <= :toDate
      AND (
           :#{#cursor} IS NULL
        OR t.dueDate > :#{#cursor.dueDate}
        OR (
             t.dueDate = :#{#cursor.dueDate}
             AND t.priorityValue > :#{#cursor.priorityValue}
           )
        OR (
             t.dueDate = :#{#cursor.dueDate}
             AND t.priorityValue = :#{#cursor.priorityValue}
             AND t.id > :#{#cursor.id}
           )
      )
    ORDER BY
        t.dueDate ASC,
        t.priorityValue ASC,
        t.id ASC
    """)
  List<Task> searchTasksWithCursor(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("cursor") TaskCursor cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    WHERE t.deletedAt IS NULL
      AND t.assignee.id = :userId
      AND (
           LOWER(t.content) LIKE CONCAT('%', :keyword, '%') OR
           LOWER(t.taskCode) LIKE CONCAT('%', :keyword, '%')
      )
      AND t.dueDate >= :fromDate
      AND t.startDate <= :toDate
    """)
  long countTasks(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query("""
    SELECT t
    FROM Task t
    WHERE t.deletedAt <= :cutoffTime
    """)
  List<Task> getDeletedTasksBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

  @Query(
      """
    SELECT t
    FROM Task t
    WHERE t.parentTask.id = :parentId
    AND t.deletedAt IS NULL
    ORDER BY t.startDate ASC
    """)
  List<Task> findAllNotDeletedChildTasks(@Param("parentId") UUID parentId);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt IS NULL
    AND t.dueDate >= :fromDate
    AND t.startDate <= :toDate
    """)
  List<Task> findAllByTeamIdInRange(
      @Param("teamId") UUID teamId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query(
      """
    SELECT t
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt IS NULL
    AND t.dueDate >= :fromDate
    AND t.startDate <= :toDate
    """)
  List<Task> findAllByTeamIdAndUserIdInRange(
      @Param("teamId") UUID teamId,
      @Param("userId") UUID userId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query(
      """
    SELECT COUNT(t)
    FROM Task t
    JOIN t.plan p
    WHERE p.team.id = :teamId
    AND t.deletedAt IS NULL
    AND t.dueDate >= :fromDate
    AND t.startDate <= :toDate
    AND t.assignee.id = :userId
    """)
  long countCompletedTasksInRange(
      @Param("teamId") UUID teamId,
      @Param("userId") UUID userId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query(
      """
    SELECT new com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto(
        u.id,
        u.name,
        u.avatarUrl,
        COUNT(t)
    )
    FROM TeamUser tu JOIN tu.user u
    LEFT JOIN Task t ON t.assignee = u
        AND t.plan.team.id = :teamId
        AND t.deletedAt IS NULL
        AND t.completedAt IS NOT NULL
        AND t.dueDate >= :fromDate
        AND t.startDate <= :toDate
    WHERE tu.team.id = :teamId
    GROUP BY u.id, u.name, u.avatarUrl
    ORDER BY COUNT(t) DESC, u.id ASC
    """)
  List<TaskStatisticsResponseDto> getTaskStatistics(
      @Param("teamId") UUID teamId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto(
        u.id,
        u.name,
        u.avatarUrl,
        COUNT(t)
    )
    FROM TeamUser tu JOIN tu.user u
    LEFT JOIN Task t ON t.assignee = u
        AND t.plan.team.id = :teamId
        AND t.deletedAt IS NULL
        AND t.completedAt IS NOT NULL
        AND t.dueDate >= :fromDate
        AND t.startDate <= :toDate
    WHERE tu.team.id = :teamId
    GROUP BY u.id, u.name, u.avatarUrl
    HAVING (
        COUNT(t) < :cursorCount
        OR (COUNT(t) = :cursorCount AND u.id > :cursorUserId)
    )
    ORDER BY COUNT(t) DESC, u.id ASC
    """)
  List<TaskStatisticsResponseDto> getTaskStatisticsWithCursor(
      @Param("teamId") UUID teamId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("cursorCount") Long cursorCount,
      @Param("cursorUserId") UUID cursorUserId,
      Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto(
        u.id,
        u.name,
        u.avatarUrl,
        COUNT(t)
    )
    FROM TeamUser tu JOIN tu.user u
    LEFT JOIN Task t ON t.assignee = u
        AND t.plan.team.id = :teamId
        AND t.deletedAt IS NULL
        AND t.completedAt IS NOT NULL
        AND t.dueDate >= :fromDate
        AND t.startDate <= :toDate
    WHERE tu.team.id = :teamId
    AND LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
    GROUP BY u.id, u.name, u.avatarUrl
    ORDER BY COUNT(t) DESC, u.id ASC
    """)
  List<TaskStatisticsResponseDto> searchTaskStatistics(
      @Param("teamId") UUID teamId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto(
        u.id,
        u.name,
        u.avatarUrl,
        COUNT(t)
    )
    FROM TeamUser tu JOIN tu.user u
    LEFT JOIN Task t ON t.assignee = u
        AND t.plan.team.id = :teamId
        AND t.deletedAt IS NULL
        AND t.completedAt IS NOT NULL
        AND t.dueDate >= :fromDate
        AND t.startDate <= :toDate
    WHERE tu.team.id = :teamId
      AND LOWER(u.name) LIKE CONCAT('%', :keyword, '%')
    GROUP BY u.id, u.name, u.avatarUrl
    HAVING (
        COUNT(t) < :cursorCount
     OR (COUNT(t) = :cursorCount AND u.id > :cursorUserId)
    )
    ORDER BY COUNT(t) DESC, u.id ASC
    """)
  List<TaskStatisticsResponseDto> searchTaskStatisticsWithCursor(
      @Param("teamId") UUID teamId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("keyword") String keyword,
      @Param("cursorCount") Long cursorCount,
      @Param("cursorUserId") UUID cursorUserId,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(t) > 0
    FROM Task t
    JOIN t.plan p
    WHERE t.deletedAt IS NULL
      AND t.completedAt IS NULL
      AND t.assignee.id = :userId
      AND p.team.id = :teamId
    """)
  boolean existsRemainingTasksInTeam(UUID userId, UUID teamId);
}
