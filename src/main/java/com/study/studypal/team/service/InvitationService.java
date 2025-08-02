package com.study.studypal.team.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.Invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.Invitation.response.ListInvitationResponseDto;
import com.study.studypal.team.entity.TeamUser;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InvitationService {
    InvitationResponseDto sendInvitation(TeamUser membership, SendInvitationRequestDto request);
    ListInvitationResponseDto getInvitations(UUID userId, LocalDateTime cursor, int size);
    ActionResponseDto replyToInvitation(UUID invitationId, UUID userId, boolean accept);

    UUID getTeamIdByInvitationId(UUID invitationId);
}
