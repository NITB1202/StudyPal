package com.study.studypal.session.service;

import java.time.LocalDateTime;

public interface SessionInternalService {
  void deleteSessionsBefore(LocalDateTime cutoffTime);
}
