package com.study.studypal.team.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.Invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.Invitation.response.ListInvitationResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InvitationService {
    InvitationResponseDto sendInvitation(UUID userId, SendInvitationRequestDto request);
    ListInvitationResponseDto getInvitations(UUID userId, LocalDateTime cursor, int size);
    ActionResponseDto replyToInvitation(UUID invitationId, UUID userId, boolean accept);
}
