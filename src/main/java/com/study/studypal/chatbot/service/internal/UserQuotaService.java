package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.entity.UserQuota;
import java.util.UUID;

public interface UserQuotaService {
  UserQuota getById(UUID id);

  void initializeUsage(UUID userId);

  void validateTokenQuota(UUID userId, AIRequestDto request);

  void saveMessageUsage(ChatMessage message, long duration);
}
