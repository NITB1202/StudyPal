package com.study.studypal.session.service.impl;

import com.study.studypal.session.repository.SessionRepository;
import com.study.studypal.session.service.SessionInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionInternalServiceImpl implements SessionInternalService {
  private final SessionRepository sessionRepository;

  @Override
  @Transactional
  public void deleteSessionsBefore(LocalDateTime cutoffTime) {
    sessionRepository.deleteAllByStudiedAtBefore(cutoffTime);
  }
}
