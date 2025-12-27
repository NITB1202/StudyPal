package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ChatbotPersistenceService {
  void persistChatResult(
      UUID userId,
      ChatRequestDto request,
      LocalDateTime userSentAt,
      AIResponseDto finalResponse,
      Long duration,
      List<MultipartFile> attachments,
      String idempotencyKey);
}
