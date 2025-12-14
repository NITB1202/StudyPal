package com.study.studypal.chatbot.dto.external;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIRequestDto {
  private String prompt;

  private String context;

  private List<String> attachments;
}
