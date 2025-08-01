package com.study.studypal.team.repository;

import com.study.studypal.team.entity.Invitation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    boolean existsByInviteeIdAndTeamId(UUID inviteeId, UUID teamId);
    List<Invitation> findByInviteeId(UUID inviteeId, Pageable pageable);
    List<Invitation> findByInviteeIdAndInvitedAtLessThan(UUID inviteeId, LocalDateTime invitedAt, Pageable pageable);
    long countByInviteeId(UUID inviteeId);
    void deleteAllByInvitedAtBefore(LocalDateTime time);
}