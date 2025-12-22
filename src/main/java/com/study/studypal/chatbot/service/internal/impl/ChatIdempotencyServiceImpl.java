package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.dto.internal.ChatIdempotencyResult;
import com.study.studypal.chatbot.entity.ChatIdempotency;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.enums.TransactionStatus;
import com.study.studypal.chatbot.exception.ChatIdempotencyErrorCode;
import com.study.studypal.chatbot.mapper.ChatbotMapper;
import com.study.studypal.chatbot.repository.ChatIdempotencyRepository;
import com.study.studypal.chatbot.service.internal.ChatIdempotencyService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatIdempotencyServiceImpl implements ChatIdempotencyService {
  private final ChatIdempotencyRepository repository;
  private final ChatbotMapper mapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public ChatIdempotencyResult tryAcquire(UUID userId, String idempotencyKey) {
    Optional<ChatIdempotency> existingRecord =
        repository.findByUserIdAndIdempotencyKey(userId, idempotencyKey);

    if (existingRecord.isPresent()) {
      ChatIdempotency idempotencyRecord = existingRecord.get();
      return switch (idempotencyRecord.getTransactionStatus()) {
        case PROCESSING -> ChatIdempotencyResult.processing();
        case DONE ->
            ChatIdempotencyResult.done(
                mapper.toChatResponseDto(idempotencyRecord.getResponseMessage()));
        case FAILED -> ChatIdempotencyResult.failed();
      };
    }

    try {
      ChatIdempotency newProcessingRecord = buildProcessingRecord(userId, idempotencyKey);
      repository.save(newProcessingRecord);
      return ChatIdempotencyResult.acquired();
    } catch (DataIntegrityViolationException e) {
      return ChatIdempotencyResult.processing();
    }
  }

  @Override
  public void markAsDone(UUID userId, String idempotencyKey, ChatMessage response) {
    ChatIdempotency chatIdempotency =
        repository
            .findByUserIdAndIdempotencyKeyForUpdate(userId, idempotencyKey)
            .orElseThrow(
                () -> new BaseException(ChatIdempotencyErrorCode.CHAT_IDEMPOTENCY_NOT_FOUND));

    chatIdempotency.setTransactionStatus(TransactionStatus.DONE);
    chatIdempotency.setResponseMessage(response);

    repository.save(chatIdempotency);
  }

  @Override
  public void markAsFailed(UUID userId, String idempotencyKey) {
    ChatIdempotency chatIdempotency =
        repository
            .findByUserIdAndIdempotencyKeyForUpdate(userId, idempotencyKey)
            .orElseThrow(
                () -> new BaseException(ChatIdempotencyErrorCode.CHAT_IDEMPOTENCY_NOT_FOUND));

    chatIdempotency.setTransactionStatus(TransactionStatus.FAILED);
    repository.save(chatIdempotency);
  }

  private ChatIdempotency buildProcessingRecord(UUID userId, String idempotencyKey) {
    User user = entityManager.getReference(User.class, userId);

    return ChatIdempotency.builder()
        .user(user)
        .idempotencyKey(idempotencyKey)
        .transactionStatus(TransactionStatus.PROCESSING)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
