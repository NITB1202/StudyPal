package com.study.studypal.team.dto.invitation.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
