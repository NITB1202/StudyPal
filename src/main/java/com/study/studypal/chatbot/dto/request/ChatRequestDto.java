package com.study.studypal.chatbot.dto.request;

import com.study.studypal.chatbot.enums.ContextType;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
  @NotBlank(message = "Prompt is required")
  private String prompt;

  private UUID contextId;

  private ContextType contextType;
}
