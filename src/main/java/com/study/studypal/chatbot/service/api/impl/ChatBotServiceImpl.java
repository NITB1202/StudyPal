package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.client.AIRestClient;
import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.dto.external.ExtractedFile;
import com.study.studypal.chatbot.dto.internal.ChatIdempotencyResult;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.dto.response.ChatMessageAttachmentResponseDto;
import com.study.studypal.chatbot.dto.response.ChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.dto.response.ListChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.MessageContextResponseDto;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.ChatIdempotencyErrorCode;
import com.study.studypal.chatbot.mapper.ChatbotMapper;
import com.study.studypal.chatbot.service.api.ChatBotService;
import com.study.studypal.chatbot.service.internal.ChatIdempotencyService;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.chatbot.service.internal.ChatMessageService;
import com.study.studypal.chatbot.service.internal.UserQuotaService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.FileUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {
  private final ChatMessageService messageService;
  private final ChatMessageAttachmentService attachmentService;
  private final UserQuotaService usageService;
  private final ChatMessageContextService contextService;
  private final ModelMapper modelMapper;
  private final ChatbotMapper mapper;
  private final AIRestClient aiRestClient;
  private final ChatIdempotencyService idempotencyService;

  @Override
  public ChatResponseDto sendMessage(
      UUID userId, ChatRequestDto request, List<MultipartFile> attachments, String idempotencyKey) {
    // Save user send time
    LocalDateTime userSentAt = LocalDateTime.now();

    // Try to acquire idempotency record
    ChatIdempotencyResult acquireResult = idempotencyService.tryAcquire(userId, idempotencyKey);

    if (acquireResult.isDone()) {
      return acquireResult.getResponse();
    }

    if (acquireResult.isProcessing()) {
      throw new BaseException(ChatIdempotencyErrorCode.CHAT_IDEMPOTENCY_REQUEST_IN_PROGRESS);
    }

    // FAILED or ACQUIRED -> continue to prepare request
    AIRequestDto aiRequest;
    try {
      String context =
          contextService.validateAndSerializeContext(
              userId, request.getContextId(), request.getContextType());
      List<ExtractedFile> extractedAttachments =
          attachmentService.validateAndExtractAttachments(attachments);
      String normalizedPrompt = normalizePrompt(request.getPrompt());

      aiRequest = mapper.toAIRequestDto(normalizedPrompt, context, extractedAttachments);
      usageService.validateAndSetMaxOutputTokens(userId, aiRequest);
    } catch (Exception e) {
      idempotencyService.markAsFailed(userId, idempotencyKey);
      throw e;
    }

    // Call AI service
    AIResponseDto aiResponse;
    long duration;
    try {
      long start = System.currentTimeMillis();
      aiResponse = sendRequest(aiRequest);
      duration = System.currentTimeMillis() - start;
    } catch (Exception e) {
      idempotencyService.markAsFailed(userId, idempotencyKey);
      throw e;
    }

    // Persist result
    try {
      ChatMessage message = messageService.saveMessage(userId, request, userSentAt);
      usageService.saveMessageUsage(message, aiResponse, duration);
      attachmentService.saveAttachments(message, attachments);

      ChatMessage reply = messageService.saveReply(userId, aiResponse, LocalDateTime.now());
      idempotencyService.markAsDone(userId, idempotencyKey, reply);

      return mapper.toChatResponseDto(reply);
    } catch (Exception e) {
      idempotencyService.markAsFailed(userId, idempotencyKey);
      throw e;
    }
  }

  @Override
  public ListChatMessageResponseDto getMessages(UUID userId, LocalDateTime cursor, int size) {
    List<ChatMessage> chatMessages = messageService.getMessages(userId, cursor, size);
    List<ChatMessageResponseDto> responseDto =
        chatMessages.stream().map(this::toResponseDto).toList();

    long total = messageService.countMessages(userId);

    LocalDateTime nextCursor =
        chatMessages.size() == size
            ? chatMessages.get(chatMessages.size() - 1).getCreatedAt()
            : null;

    return ListChatMessageResponseDto.builder()
        .messages(responseDto)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public UserQuotaUsageResponseDto getUsage(UUID userId) {
    UserQuota userQuota = usageService.getById(userId);
    return modelMapper.map(userQuota, UserQuotaUsageResponseDto.class);
  }

  private ChatMessageResponseDto toResponseDto(ChatMessage message) {
    ChatMessageResponseDto responseDto = modelMapper.map(message, ChatMessageResponseDto.class);

    String contextCode =
        contextService.getContextCode(message.getContextId(), message.getContextType());
    MessageContextResponseDto context =
        MessageContextResponseDto.builder()
            .id(message.getContextId())
            .type(message.getContextType())
            .code(contextCode)
            .build();

    List<ChatMessageAttachment> attachments = attachmentService.getByMessageId(message.getId());
    List<ChatMessageAttachmentResponseDto> attachmentsResponseDto =
        modelMapper.map(
            attachments, new TypeToken<List<ChatMessageAttachmentResponseDto>>() {}.getType());

    responseDto.setContext(context);
    responseDto.setAttachments(attachmentsResponseDto);

    return responseDto;
  }

  private String normalizePrompt(String prompt) {
    return FileUtils.normalizeText(prompt);
  }

  private AIResponseDto sendRequest(AIRequestDto request) {
    return aiRestClient.ask(request);
  }
}
