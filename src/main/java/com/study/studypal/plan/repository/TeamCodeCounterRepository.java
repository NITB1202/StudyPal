package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TeamCodeCounter;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamCodeCounterRepository extends JpaRepository<TeamCodeCounter, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM TeamCodeCounter c WHERE c.team.id = :teamId")
  Optional<TeamCodeCounter> findByTeamIdForUpdate(UUID teamId);
}
