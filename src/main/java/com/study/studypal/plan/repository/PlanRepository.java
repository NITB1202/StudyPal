package com.study.studypal.plan.repository;

import com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto;
import com.study.studypal.plan.entity.Plan;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    SELECT new com.study.studypal.plan.dto.plan.response.PlanSummaryResponseDto(
        p.id,
        p.title,
        p.progress,
        MIN(t.startDate),
        MAX(t.dueDate),
        CASE WHEN SUM(CASE WHEN t.assignee.id = :userId THEN 1 ELSE 0 END) > 0 THEN true ELSE false END
    )
    FROM Plan p
    LEFT JOIN p.tasks t
    WHERE p.isDeleted = false
    AND p.team.id = :teamId
    GROUP BY p.id, p.title, p.progress
    HAVING MAX(t.dueDate) >= :startOfDay AND MIN(t.startDate) <= :endOfDay
    """)
  List<PlanSummaryResponseDto> findPlansOnDate(
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
    """)
  List<LocalDateTime> findPlanDueDatesByTeamIdInMonth(
      @Param("teamId") UUID teamId, @Param("month") int month, @Param("year") int year);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Plan p WHERE p.id = :id")
  Optional<Plan> findByIdForUpdate(UUID id);
}
