package com.study.studypal.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.chatbot.enums.Sender;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponseDto {
  private UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Sender sender;

  private String message;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  private MessageContextResponseDto context;

  private List<ChatMessageAttachmentResponseDto> attachments;
}
