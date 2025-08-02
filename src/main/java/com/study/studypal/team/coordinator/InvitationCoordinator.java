package com.study.studypal.team.coordinator;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.Invitation.response.InvitationResponseDto;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.service.InvitationService;
import com.study.studypal.team.service.TeamInternalService;
import com.study.studypal.team.service.TeamMembershipInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationCoordinator {
    private final TeamInternalService teamService;
    private final TeamMembershipInternalService teamMembershipService;
    private final InvitationService invitationService;

    public InvitationResponseDto sendInvitation(UUID userId, SendInvitationRequestDto request) {
        teamMembershipService.validateInviteMemberPermission(userId, request.getTeamId(), request.getInviteeId());
        TeamUser membership = teamMembershipService.getMemberShip(request.getTeamId(), userId);
        return invitationService.sendInvitation(membership, request);
    }

    public ActionResponseDto replyToInvitation(UUID invitationId, UUID userId, boolean accept) {
        UUID teamId = invitationService.getTeamIdByInvitationId(invitationId);
        ActionResponseDto response = invitationService.replyToInvitation(invitationId, userId, accept);

        if(accept) {
            teamMembershipService.createMembership(teamId, userId, TeamRole.MEMBER);
            teamService.increaseMember(teamId);
        }

        return response;
    }
}
