package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.enums.Sender;
import com.study.studypal.chatbot.repository.ChatMessageRepository;
import com.study.studypal.chatbot.service.internal.ChatMessageService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
  private final ChatMessageRepository chatMessageRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public ChatMessage saveMessage(UUID userId, ChatRequestDto request, LocalDateTime sentAt) {
    User user = entityManager.getReference(User.class, userId);

    ChatMessage message =
        ChatMessage.builder()
            .user(user)
            .sender(Sender.USER)
            .message(request.getPrompt())
            .contextId(request.getContextId())
            .contextType(request.getContextType())
            .createdAt(sentAt)
            .build();

    return chatMessageRepository.save(message);
  }

  @Override
  @Transactional
  public ChatMessage saveReply(UUID userId, AIResponseDto response, LocalDateTime repliedAt) {
    User user = entityManager.getReference(User.class, userId);

    ChatMessage message =
        ChatMessage.builder()
            .user(user)
            .sender(Sender.AI)
            .message(response.getReply())
            .createdAt(repliedAt)
            .build();

    return chatMessageRepository.save(message);
  }

  @Override
  public List<ChatMessage> getMessages(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);
    return cursor == null
        ? chatMessageRepository.findByUserId(userId, pageable)
        : chatMessageRepository.findByUserIdWithCursor(userId, cursor, pageable);
  }

  @Override
  public long countMessages(UUID userId) {
    return chatMessageRepository.countByUserId(userId);
  }
}
