package com.study.studypal.chatbot.dto.response;

import java.util.List;
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
public class ListChatMessageResponseDto {
  private List<ChatMessageResponseDto> messages;

  private long total;

  private Long cursor;
}
