package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.client.AIClient;
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
import com.study.studypal.chatbot.service.api.ChatbotService;
import com.study.studypal.chatbot.service.internal.ChatIdempotencyService;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.chatbot.service.internal.ChatMessageService;
import com.study.studypal.chatbot.service.internal.ChatbotPersistenceService;
import com.study.studypal.chatbot.service.internal.UserQuotaService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.FileUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
  private final ChatMessageService messageService;
  private final ChatMessageAttachmentService attachmentService;
  private final UserQuotaService usageService;
  private final ChatMessageContextService contextService;
  private final ChatIdempotencyService idempotencyService;
  private final ChatbotPersistenceService persistenceService;
  private final ModelMapper modelMapper;
  private final ChatbotMapper mapper;
  private final AIClient aiClient;

  @Override
  public Flux<ServerSentEvent<ChatResponseDto>> sendMessage(
      UUID userId, ChatRequestDto request, List<MultipartFile> attachments, String idempotencyKey) {
    // Save user send time
    LocalDateTime userSentAt = LocalDateTime.now();

    // Try to acquire idempotency record
    ChatIdempotencyResult acquireResult = idempotencyService.tryAcquire(userId, idempotencyKey);

    if (acquireResult.isDone()) {
      ServerSentEvent<ChatResponseDto> sse =
          ServerSentEvent.builder(acquireResult.getResponse()).build();
      return Flux.just(sse);
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
    long start = System.currentTimeMillis();
    StringBuilder messageBuilder = new StringBuilder();
    AtomicLong inputTokens = new AtomicLong(0);
    AtomicLong outputTokens = new AtomicLong(0);

    return sendRequest(aiRequest)
        .doOnNext(
            sse -> {
              AIResponseDto chunk = sse.data();
              if (chunk != null) {
                messageBuilder.append(chunk.getReply());
                inputTokens.set(chunk.getInputTokens());
                outputTokens.set(chunk.getOutputTokens());
              }
            })
        .map(ServerSentEvent::data)
        .filter(Objects::nonNull)
        .map(chunk -> ServerSentEvent.builder(mapper.toChatResponseDto(chunk)).build())

        // When the stream complete -> persist result
        .doOnComplete(
            () -> {
              long duration = System.currentTimeMillis() - start;

              try {
                AIResponseDto finalResponse =
                    mapper.toAIResponseDto(
                        messageBuilder.toString(), inputTokens.get(), outputTokens.get());

                persistenceService.persistChatResult(
                    userId,
                    request,
                    userSentAt,
                    finalResponse,
                    duration,
                    attachments,
                    idempotencyKey);

              } catch (Exception e) {
                idempotencyService.markAsFailed(userId, idempotencyKey);
              }
            })
        .doOnError(err -> idempotencyService.markAsFailed(userId, idempotencyKey));
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

  private Flux<ServerSentEvent<AIResponseDto>> sendRequest(AIRequestDto request) {
    return aiClient.ask(request);
  }
}
