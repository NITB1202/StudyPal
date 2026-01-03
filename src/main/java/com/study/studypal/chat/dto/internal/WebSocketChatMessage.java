package com.study.studypal.chat.dto.internal;

import com.study.studypal.chat.enums.ChatEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketChatMessage {
  private ChatEventType type;

  private Object data;
}
