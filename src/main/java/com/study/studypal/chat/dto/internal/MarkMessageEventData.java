package com.study.studypal.chat.dto.internal;

import java.util.UUID;
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
public class MarkMessageEventData {
  private UUID messageId;

  private UUID userId;

  private String name;

  private String avatarUrl;
}
