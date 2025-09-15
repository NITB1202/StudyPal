package com.study.studypal.team.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.service.internal.TeamNotificationSettingsInternalService;
import com.study.studypal.team.dto.invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.invitation.response.ListInvitationResponseDto;
import com.study.studypal.team.entity.Invitation;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.exception.InvitationErrorCode;
import com.study.studypal.team.repository.InvitationRepository;
import com.study.studypal.team.service.api.InvitationService;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
  private final InvitationRepository invitationRepository;
  private final TeamMembershipInternalService teamMembershipService;
  private final TeamInternalService teamService;
  private final TeamNotificationSettingsInternalService teamNotificationSettingsService;
  private final ModelMapper modelMapper;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  @CacheEvict(value = CacheNames.INVITATIONS, key = "@keys.of(#request.inviteeId)")
  public InvitationResponseDto sendInvitation(UUID userId, SendInvitationRequestDto request) {
    teamMembershipService.validateInviteMemberPermission(
        userId, request.getTeamId(), request.getInviteeId());

    if (invitationRepository.existsByInviteeIdAndTeamId(
        request.getInviteeId(), request.getTeamId())) {
      throw new BaseException(InvitationErrorCode.INVITEE_ALREADY_INVITED);
    }

    User inviter = entityManager.getReference(User.class, userId);
    User invitee = entityManager.getReference(User.class, request.getInviteeId());
    Team team = entityManager.getReference(Team.class, request.getTeamId());

    Invitation invitation =
        Invitation.builder()
            .inviter(inviter)
            .invitee(invitee)
            .team(team)
            .invitedAt(LocalDateTime.now())
            .build();

    // Handle race condition
    try {
      invitationRepository.save(invitation);
    } catch (DataIntegrityViolationException e) {
      throw new BaseException(InvitationErrorCode.INVITEE_ALREADY_INVITED);
    }

    return modelMapper.map(invitation, InvitationResponseDto.class);
  }

  @Override
  @Cacheable(
      value = CacheNames.INVITATIONS,
      key = "@keys.of(#userId)",
      condition = "#cursor == null && #size == 10")
  public ListInvitationResponseDto getInvitations(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    List<Invitation> invitations =
        cursor != null
            ? invitationRepository.findByInviteeIdAndInvitedAtLessThanOrderByInvitedAtDesc(
                userId, cursor, pageable)
            : invitationRepository.findByInviteeIdOrderByInvitedAtDesc(userId, pageable);

    List<InvitationResponseDto> dto =
        modelMapper.map(invitations, new TypeToken<List<InvitationResponseDto>>() {}.getType());
    long total = invitationRepository.countByInviteeId(userId);
    LocalDateTime nextCursor =
        !dto.isEmpty() && dto.size() == size ? dto.get(dto.size() - 1).getInvitedAt() : null;

    return ListInvitationResponseDto.builder()
        .invitations(dto)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = CacheNames.INVITATIONS, key = "@keys.of(#userId)"),
        @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)")
      })
  public ActionResponseDto replyToInvitation(UUID invitationId, UUID userId, boolean accept) {
    Invitation invitation =
        invitationRepository
            .findById(invitationId)
            .orElseThrow(() -> new BaseException(InvitationErrorCode.INVITATION_NOT_FOUND));

    if (!userId.equals(invitation.getInvitee().getId())) {
      throw new BaseException(InvitationErrorCode.PERMISSION_REPLY_INVITATION_DENIED);
    }

    if (accept) {
      UUID teamId = invitation.getTeam().getId();
      teamMembershipService.createMembership(teamId, userId, TeamRole.MEMBER);
      teamService.increaseMember(teamId);
      teamNotificationSettingsService.createSettings(userId, teamId);
    }

    invitationRepository.delete(invitation);

    return ActionResponseDto.builder().success(true).message("Reply successfully.").build();
  }
}
