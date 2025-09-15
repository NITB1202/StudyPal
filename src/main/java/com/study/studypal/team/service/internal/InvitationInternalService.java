package com.study.studypal.team.service.internal;

import java.time.LocalDateTime;

public interface InvitationInternalService {
  void deleteInvitationBefore(LocalDateTime cutoffTime);
}
