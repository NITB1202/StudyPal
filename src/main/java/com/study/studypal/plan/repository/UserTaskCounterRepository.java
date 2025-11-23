package com.study.studypal.plan.repository;

import com.study.studypal.plan.entity.UserTaskCounter;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskCounterRepository extends JpaRepository<UserTaskCounter, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM UserTaskCounter u WHERE u.id = :id")
  Optional<UserTaskCounter> findByIdForUpdate(UUID id);
}
