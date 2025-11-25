package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.PlanHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanHistoryRepository extends JpaRepository<PlanHistory, UUID> {
  List<PlanHistory> findByPlanIdOrderByTimestampDesc(UUID planId, Pageable pageable);

  @Query(
      """
    SELECT h
    FROM PlanHistory h
    WHERE h.plan.id = :planId
    AND h.timestamp < :cursor
    ORDER BY h.timestamp DESC
    """)
  List<PlanHistory> findByPlanIdWithCursor(
      @Param("planId") UUID planId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  long countByPlanId(UUID planId);
}
