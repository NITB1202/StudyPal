package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatMessageService {
  ChatMessage saveMessage(UUID userId, ChatRequestDto request, LocalDateTime sentAt);

  ChatMessage saveReply(UUID userId, AIResponseDto response, LocalDateTime repliedAt);

  List<ChatMessage> getMessages(UUID userId, LocalDateTime cursor, int size);

  long countMessages(UUID userId);

  List<ChatMessage> getMessagesBefore(LocalDateTime time);

  void deleteMessages(List<ChatMessage> messages);
}
