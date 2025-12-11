package com.study.studypal.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.chatbot.enums.ContextType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageContextResponseDto {
  private UUID id;

  private String code;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ContextType type;
}
