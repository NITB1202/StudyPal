package com.study.studypal.chatbot.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AIResponseDto {
  private String reply;

  @JsonProperty("input_tokens")
  private Long inputTokens;

  @JsonProperty("output_tokens")
  private Long outputTokens;
}
