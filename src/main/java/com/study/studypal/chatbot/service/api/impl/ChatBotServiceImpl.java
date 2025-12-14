package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
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
import com.study.studypal.chatbot.enums.Sender;
import com.study.studypal.chatbot.mapper.ChatbotMapper;
import com.study.studypal.chatbot.repository.ChatMessageRepository;
import com.study.studypal.chatbot.service.api.ChatBotService;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.chatbot.service.internal.UserQuotaService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {
  private final ChatMessageRepository chatMessageRepository;
  private final ChatMessageAttachmentService attachmentService;
  private final UserQuotaService usageService;
  private final ChatMessageContextService contextService;
  private final ModelMapper modelMapper;
  private final ChatbotMapper mapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public ChatResponseDto sendMessage(
      UUID userId, ChatRequestDto request, List<MultipartFile> attachments) {
    String context =
        contextService.validateAndSerializeContext(
            request.getContextId(), request.getContextType());
    List<String> attachmentContents =
        attachmentService.validateAndSerializeAttachments(attachments);
    String normalizedPrompt = normalizePrompt(request.getPrompt());

    AIRequestDto aiRequest = mapper.toAIRequestDto(normalizedPrompt, context, attachmentContents);
    usageService.validateAndSetMaxOutputTokens(userId, aiRequest);

    ChatMessage message = saveMessage(userId, request);

    long start = System.currentTimeMillis();
    AIResponseDto aiResponse = sendRequest(aiRequest);
    long duration = System.currentTimeMillis() - start;

    usageService.saveMessageUsage(message, aiResponse, duration);
    attachmentService.saveAttachments(message, attachments);

    ChatMessage reply = saveReply(userId, aiResponse);
    return mapper.toChatResponseDto(reply);
  }

  @Override
  public ListChatMessageResponseDto getMessages(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    List<ChatMessage> chatMessages =
        cursor == null
            ? chatMessageRepository.findByUserId(userId, pageable)
            : chatMessageRepository.findByUserIdWithCursor(userId, cursor, pageable);

    List<ChatMessageResponseDto> responseDto =
        chatMessages.stream().map(this::toResponseDto).toList();

    long total = chatMessageRepository.countByUserId(userId);

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
    return AIResponseDto.builder()
        .reply("Mock reply")
        .inputTokens(1000L)
        .outputTokens(5000L)
        .build();
  }

  private ChatMessage saveMessage(UUID userId, ChatRequestDto request) {
    User user = entityManager.getReference(User.class, userId);

    ChatMessage message =
        ChatMessage.builder()
            .user(user)
            .sender(Sender.USER)
            .message(request.getPrompt())
            .contextId(request.getContextId())
            .contextType(request.getContextType())
            .createdAt(LocalDateTime.now())
            .build();

    return chatMessageRepository.save(message);
  }

  private ChatMessage saveReply(UUID userId, AIResponseDto response) {
    User user = entityManager.getReference(User.class, userId);

    ChatMessage message =
        ChatMessage.builder()
            .user(user)
            .sender(Sender.AI)
            .message(response.getReply())
            .createdAt(LocalDateTime.now())
            .build();

    return chatMessageRepository.save(message);
  }
}
