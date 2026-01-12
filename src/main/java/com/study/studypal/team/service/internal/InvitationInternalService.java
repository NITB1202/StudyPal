package com.study.studypal.team.service.internal;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InvitationInternalService {
  void deleteInvitationBefore(LocalDateTime cutoffTime);

  long countByUserId(UUID userId);
}
