package com.study.studypal.chatbot.service;

import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.dto.response.ListChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ChatBotService {
  ChatResponseDto sendMessage(UUID userId, ChatRequestDto request, List<MultipartFile> attachments);

  ListChatMessageResponseDto getMessages(UUID userId, Long cursor, int size);

  UserQuotaUsageResponseDto getUsage(UUID userId);
}
