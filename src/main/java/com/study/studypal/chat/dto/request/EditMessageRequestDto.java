package com.study.studypal.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class EditMessageRequestDto {
  @NotBlank(message = "Content is required")
  @Size(max = 4000, message = "Message content must not exceed 4000 characters")
  private String content;
}
