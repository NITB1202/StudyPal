package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.Plan;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
  @Query("""
    SELECT p FROM Plan p
    JOIN FETCH p.team
    WHERE p.id = :planId
    """)
  Optional<Plan> findByIdWithTeam(UUID planId);
}
