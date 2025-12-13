package com.study.studypal.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.chatbot.enums.ContextType;
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
public class MessageContextResponseDto {
  private UUID id;

  private String code;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ContextType type;
}
