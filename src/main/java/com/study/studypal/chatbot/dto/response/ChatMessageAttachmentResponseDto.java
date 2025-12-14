package com.study.studypal.chatbot.dto.response;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageAttachmentResponseDto {
  private UUID id;

  private String name;

  private String url;

  private Long size;
}
