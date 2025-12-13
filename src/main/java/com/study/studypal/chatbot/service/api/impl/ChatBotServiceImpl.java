package com.study.studypal.chatbot.service.api.impl;

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
import com.study.studypal.chatbot.repository.ChatMessageRepository;
import com.study.studypal.chatbot.service.api.ChatBotService;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.ChatMessageContextService;
import com.study.studypal.chatbot.service.internal.UserQuotaUsageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {
  private final ChatMessageRepository chatMessageRepository;
  private final ChatMessageAttachmentService attachmentService;
  private final UserQuotaUsageService usageService;
  private final ChatMessageContextService contextService;
  private final ModelMapper modelMapper;

  @Override
  public ChatResponseDto sendMessage(
      UUID userId, ChatRequestDto request, List<MultipartFile> attachments) {
    String context =
        contextService.validateAndSerializeContext(
            request.getContextId(), request.getContextType());
    List<String> attachmentContents =
        attachmentService.validateAndSerializeAttachments(attachments);
    String normalizedPrompt = normalizePrompt(request.getPrompt());

    usageService.validateTokenQuota(normalizedPrompt, context, attachmentContents);

    //    updateQuotaUsage(userId);
    //
    //    UUID messageId = saveMessage(request);
    //    saveMessageUsage();
    //    saveAttachments(messageId, attachments);

    return null;
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
    return "";
  }
}
