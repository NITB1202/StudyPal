package com.study.studypal.chat.event;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMentionedEvent {
  private UUID userId;
  private UUID teamId;
  private List<UUID> memberIds;
}

// Message: User01 mentioned you in Team 01 chat. -> Linked: teamId
