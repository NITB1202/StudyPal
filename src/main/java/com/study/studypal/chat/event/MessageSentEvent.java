package com.study.studypal.chat.event;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageSentEvent {
  private UUID teamId;
  private UUID userId;
}

// Message: User01 sent new message in Team01 -> Linked: teamId
