package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.dto.internal.ChatIdempotencyResult;
import java.util.UUID;

public interface ChatIdempotencyService {
  ChatIdempotencyResult tryAcquire(UUID userId, String idempotencyKey);

  void markAsDone(UUID userId, String idempotencyKey);

  void markAsFailed(UUID userId, String idempotencyKey);
}
