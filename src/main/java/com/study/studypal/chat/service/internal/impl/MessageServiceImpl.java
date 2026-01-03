package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.dto.message.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.message.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.message.request.UpdateMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.repository.MessageRepository;
import com.study.studypal.chat.service.internal.MessageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
  private final MessageRepository messageRepository;

  @Override
  public Message saveMessage(UUID userId, UUID teamId, SendMessageRequestDto request) {
    return null;
  }

  @Override
  public List<Message> getMessages(UUID teamId, LocalDateTime cursor, int size) {
    return List.of();
  }

  @Override
  public Long countMessages(UUID teamId) {
    return 0L;
  }

  @Override
  public Message updateMessage(UUID userId, UUID messageId, UpdateMessageRequestDto request) {
    return null;
  }

  @Override
  public void markMessagesAsRead(UUID userId, UUID teamId, MarkMessagesAsReadRequestDto request) {}

  @Override
  public void deleteMessage(UUID userId, UUID messageId) {}
}
