package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.UserQuotaErrorCode;
import com.study.studypal.chatbot.repository.UserQuotaRepository;
import com.study.studypal.chatbot.service.api.UserQuotaUsageService;
import com.study.studypal.common.exception.BaseException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQuotaUsageServiceImpl implements UserQuotaUsageService {
  private final UserQuotaRepository userQuotaRepository;
  private final ChatbotProperties props;

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
