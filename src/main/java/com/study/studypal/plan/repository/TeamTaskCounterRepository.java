package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.TeamTaskCounter;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamTaskCounterRepository extends JpaRepository<TeamTaskCounter, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<TeamTaskCounter> findByIdForUpdate(UUID id);
}
