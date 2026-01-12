package com.study.studypal.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.chat.enums.MentionType;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;
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

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private MentionType mentionType;

  private Set<UUID> memberIds;
}
