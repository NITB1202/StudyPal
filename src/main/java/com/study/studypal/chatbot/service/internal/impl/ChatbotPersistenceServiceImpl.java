package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.service.internal.ChatIdempotencyService;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageService;
import com.study.studypal.chatbot.service.internal.ChatbotPersistenceService;
import com.study.studypal.chatbot.service.internal.UserQuotaService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatbotPersistenceServiceImpl implements ChatbotPersistenceService {
  private final ChatMessageService messageService;
  private final UserQuotaService usageService;
  private final ChatMessageAttachmentService attachmentService;
  private final ChatIdempotencyService idempotencyService;

  @Override
  @Async
  @Transactional
  public void persistChatResult(
      UUID userId,
      ChatRequestDto request,
      LocalDateTime userSentAt,
      AIResponseDto finalResponse,
      Long duration,
      List<MultipartFile> attachments,
      String idempotencyKey) {
    ChatMessage message = messageService.saveMessage(userId, request, userSentAt);
    usageService.saveMessageUsage(message, finalResponse, duration);
    attachmentService.saveAttachments(message, attachments);

    ChatMessage reply = messageService.saveReply(userId, finalResponse, LocalDateTime.now());
    idempotencyService.markAsDone(message.getUser().getId(), idempotencyKey, reply);
  }
}
