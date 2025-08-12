package com.study.studypal.team.dto.invitation.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendInvitationRequestDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "Invitee id is required")
    private UUID inviteeId;
}
