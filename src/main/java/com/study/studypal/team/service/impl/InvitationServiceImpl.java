package com.study.studypal.team.service.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BusinessException;
import com.study.studypal.common.exception.NotFoundException;
import com.study.studypal.team.dto.Invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.Invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.Invitation.response.ListInvitationResponseDto;
import com.study.studypal.team.entity.Invitation;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.repository.InvitationRepository;
import com.study.studypal.team.service.InvitationService;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final ModelMapper modelMapper;

    @Override
    public InvitationResponseDto sendInvitation(TeamUser membership, SendInvitationRequestDto request) {
        if(invitationRepository.existsByInviteeIdAndTeamId(request.getInviteeId(), request.getTeamId())) {
            throw new BusinessException("The invitee has already been invited to this team.");
        }

        User inviter = membership.getUser();
        Team team = membership.getTeam();

        Invitation invitation = Invitation.builder()
                .inviterName(inviter.getName())
                .inviterAvatarUrl(inviter.getAvatarUrl())
                .inviteeId(request.getInviteeId())
                .teamId(request.getTeamId())
                .teamName(team.getName())
                .invitedAt(LocalDateTime.now())
                .build();

        invitationRepository.save(invitation);

        return modelMapper.map(invitation, InvitationResponseDto.class);
    }

    @Override
    public ListInvitationResponseDto getInvitations(UUID userId, LocalDateTime cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<Invitation> invitations = cursor != null ?
                invitationRepository.findByInviteeIdAndInvitedAtLessThanOrderByInvitedAtDesc(userId, cursor, pageable) :
                invitationRepository.findByInviteeIdOrderByInvitedAtDesc(userId, pageable);

        List<InvitationResponseDto> dto = modelMapper.map(invitations, new TypeToken<List<InvitationResponseDto>>() {}.getType());
        long total = invitationRepository.countByInviteeId(userId);
        LocalDateTime nextCursor = !dto.isEmpty() && dto.size() == size ? dto.get(dto.size() - 1).getInvitedAt() : null;

        return ListInvitationResponseDto.builder()
                .invitations(dto)
                .total(total)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public ActionResponseDto replyToInvitation(UUID invitationId, UUID userId, boolean accept) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(
                () -> new NotFoundException("Invitation not found.")
        );

        if(!userId.equals(invitation.getInviteeId())) {
            throw new BusinessException("You are not allowed to reply this invitation.");
        }

        invitationRepository.delete(invitation);

        return ActionResponseDto.builder()
                .success(true)
                .message("Reply successfully.")
                .build();
    }

    @Override
    public UUID getTeamIdByInvitationId(UUID invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(
                () -> new NotFoundException("Invitation not found.")
        );

        return invitation.getTeamId();
    }
}
