package com.study.studypal.team.event.invitation;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvitationCreatedEvent {
  private UUID invitationId;
  private UUID inviterId;
  private UUID inviteeId;
  private UUID teamId;
}
