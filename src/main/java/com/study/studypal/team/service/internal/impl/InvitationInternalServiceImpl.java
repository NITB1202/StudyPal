package com.study.studypal.team.service.internal.impl;

import com.study.studypal.team.repository.InvitationRepository;
import com.study.studypal.team.service.internal.InvitationInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationInternalServiceImpl implements InvitationInternalService {
  private final InvitationRepository invitationRepository;

  @Override
  @Transactional
  public void deleteInvitationBefore(LocalDateTime cutoffTime) {
    invitationRepository.deleteAllByInvitedAtBefore(cutoffTime);
  }

  @Override
  public long countByUserId(UUID userId) {
    return invitationRepository.countByInviteeId(userId);
  }
}
