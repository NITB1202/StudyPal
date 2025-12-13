package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Plan;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
  @Query("""
    SELECT p FROM Plan p
    JOIN FETCH p.team
    WHERE p.id = :planId
    """)
  Optional<Plan> findByIdWithTeam(UUID planId);

  @Query(
      """
    SELECT p
    FROM Plan p
    LEFT JOIN p.tasks t
    WHERE p.isDeleted = false
    AND p.team.id = :teamId
    AND p.dueDate >= :startOfDay
    AND p.startDate <= :endOfDay
    """)
  List<Plan> findPlansOnDate(
      @Param("userId") UUID userId,
      @Param("teamId") UUID teamId,
      @Param("startOfDay") LocalDateTime startOfDay,
      @Param("endOfDay") LocalDateTime endOfDay);

  @Query(
      """
    SELECT MAX(t.dueDate)
    FROM Plan p
    JOIN p.tasks t
    WHERE p.team.id = :teamId
    AND MONTH(t.dueDate) = :month
    AND YEAR(t.dueDate) = :year
    AND p.isDeleted = false
    GROUP BY p.id
    """)
  List<LocalDateTime> findPlanDueDatesByTeamIdInMonth(
      @Param("teamId") UUID teamId, @Param("month") int month, @Param("year") int year);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Plan p WHERE p.id = :id")
  Optional<Plan> findByIdForUpdate(UUID id);

  @Query(
      """
    SELECT p
    FROM Plan p
    WHERE p.isDeleted = false
    AND p.team.id = :teamId
    AND (
        LOWER(p.title) LIKE CONCAT('%', :keyword, '%')
        OR LOWER(p.planCode) LIKE CONCAT('%', :keyword, '%')
    )
    AND p.dueDate >= :fromDate
    AND p.startDate <= :toDate
    ORDER BY
    p.dueDate ASC,
    p.id ASC
    """)
  List<Plan> searchPlans(
      @Param("teamId") UUID teamId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      """
    SELECT p
    FROM Plan p
    WHERE p.isDeleted = false
    AND p.team.id = :teamId
    AND (
        LOWER(p.title) LIKE CONCAT('%', :keyword, '%')
        OR LOWER(p.planCode) LIKE CONCAT('%', :keyword, '%')
    )
    AND p.dueDate >= :fromDate
    AND p.startDate <= :toDate
    AND (
        p.dueDate > :cursorDue
        OR (
            p.dueDate = :cursorDue
            AND p.id > :cursorId
        )
    )
    ORDER BY
    p.dueDate ASC,
    p.id ASC
    """)
  List<Plan> searchPlansWithCursor(
      @Param("teamId") UUID teamId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      @Param("cursorDue") LocalDateTime cursorDue,
      @Param("cursorId") UUID cursorId,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(p)
    FROM Plan p
    WHERE p.team.id = :teamId
    AND (
        LOWER(p.title) LIKE CONCAT('%', :keyword, '%')
        OR LOWER(p.planCode) LIKE CONCAT('%', :keyword, '%')
    )
    AND p.dueDate >= :fromDate
    AND p.startDate <= :toDate
    AND p.isDeleted = false
    """)
  long countPlans(
      @Param("teamId") UUID teamId,
      @Param("keyword") String keyword,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);
}
