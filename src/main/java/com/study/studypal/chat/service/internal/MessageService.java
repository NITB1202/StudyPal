package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.dto.request.EditMessageRequestDto;
import com.study.studypal.chat.dto.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {
  Message saveMessage(UUID userId, UUID teamId, SendMessageRequestDto request);

  List<Message> getMessages(UUID teamId, LocalDateTime cursor, int size);

  Long countMessages(UUID teamId);

  Message editMessage(UUID userId, UUID messageId, EditMessageRequestDto request);

  void markMessagesAsRead(UUID userId, UUID teamId, MarkMessagesAsReadRequestDto request);

  Message deleteMessage(UUID userId, UUID messageId);
}
