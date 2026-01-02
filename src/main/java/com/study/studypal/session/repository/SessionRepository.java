package com.study.studypal.session.repository;

import com.study.studypal.session.entity.Session;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
  List<Session> findAllByUserIdAndStudiedAtBetween(
      UUID userId, LocalDateTime fromDate, LocalDateTime toDate);
}
