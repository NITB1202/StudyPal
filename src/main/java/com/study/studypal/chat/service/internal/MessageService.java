package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.dto.message.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.message.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.message.request.UpdateMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {
  Message saveMessage(UUID userId, UUID teamId, SendMessageRequestDto request);

  List<Message> getMessages(UUID teamId, LocalDateTime cursor, int size);

  Long countMessages(UUID teamId);

  Message updateMessage(UUID userId, UUID messageId, UpdateMessageRequestDto request);

  void markMessagesAsRead(UUID userId, UUID teamId, MarkMessagesAsReadRequestDto request);

  void deleteMessage(UUID userId, UUID messageId);
}
