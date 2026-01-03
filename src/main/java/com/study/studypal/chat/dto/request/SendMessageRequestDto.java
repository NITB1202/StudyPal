package com.study.studypal.chat.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageRequestDto {
  @Size(max = 4000, message = "Message content must not exceed 4000 characters")
  private String content;
}
