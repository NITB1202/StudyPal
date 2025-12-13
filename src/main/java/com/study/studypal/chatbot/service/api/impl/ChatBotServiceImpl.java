package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.dto.response.ListChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.UserQuotaErrorCode;
import com.study.studypal.chatbot.repository.UserQuotaRepository;
import com.study.studypal.chatbot.service.api.ChatBotService;
import com.study.studypal.common.exception.BaseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {
  private final UserQuotaRepository userQuotaRepository;
  private final ChatbotProperties props;

  @Override
  public ChatResponseDto sendMessage(
      UUID userId, ChatRequestDto request, List<MultipartFile> attachments) {
    return null;
  }

  @Override
  public ListChatMessageResponseDto getMessages(UUID userId, LocalDateTime cursor, int size) {
    return null;
  }

  @Override
  public UserQuotaUsageResponseDto getUsage(UUID userId) {
    UserQuota userQuota =
        userQuotaRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(UserQuotaErrorCode.USER_QUOTA_NOT_FOUND));

    int usedRequests = (int) (userQuota.getUsedQuota() / props.getAvgTokensPerRequest());
    int totalRequests = (int) (userQuota.getDailyQuota() / props.getAvgTokensPerRequest());

    return UserQuotaUsageResponseDto.builder()
        .usedRequests(usedRequests)
        .totalRequests(totalRequests)
        .build();
  }
}
