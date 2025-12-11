package com.study.studypal.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.chatbot.enums.AttachmentType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageAttachmentResponseDto {
  private UUID id;

  private String name;

  private String url;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private AttachmentType type;

  private Long size;
}
