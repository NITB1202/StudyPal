package com.study.studypal.chatbot.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class AIRequestDto {
  private String prompt;

  private String context;

  private List<ExtractedFile> attachments;

  @JsonProperty("max_output_tokens")
  private Long maxOutputTokens;
}
