package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.entity.MessageUsage;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.UserQuotaErrorCode;
import com.study.studypal.chatbot.repository.MessageUsageRepository;
import com.study.studypal.chatbot.repository.UserQuotaRepository;
import com.study.studypal.chatbot.service.internal.UserQuotaService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class UserQuotaServiceImpl implements UserQuotaService {
  private final UserQuotaRepository userQuotaRepository;
  private final MessageUsageRepository messageUsageRepository;
  private final ChatbotProperties props;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public UserQuota getById(UUID id) {
    return userQuotaRepository
        .findById(id)
        .orElseThrow(() -> new BaseException(UserQuotaErrorCode.USER_QUOTA_NOT_FOUND));
  }

  @Override
  public void initializeUsage(UUID userId) {
    User user = entityManager.getReference(User.class, userId);
    LocalDateTime now = LocalDateTime.now();

    UserQuota userQuota =
        UserQuota.builder()
            .user(user)
            .dailyQuota(props.getDailyQuotaTokens())
            .usedQuota(0L)
            .createdAt(now)
            .updatedAt(now)
            .build();

    userQuotaRepository.save(userQuota);
  }

  @Override
  public void validateTokenQuota(UUID userId, AIRequestDto request) {
    UserQuota userQuota = getById(userId);
    int estimatedTokens = estimateToken(request);

    if (userQuota.getUsedQuota() + estimatedTokens > userQuota.getDailyQuota()) {
      throw new BaseException(UserQuotaErrorCode.TOKEN_EXCEEDED);
    }
  }

  @Override
  public void saveMessageUsage(ChatMessage message, long duration) {
    MessageUsage messageUsage = MessageUsage.builder().build();
    UUID userId = message.getUser().getId();
  }

  private int estimateToken(AIRequestDto request) {
    int chars = 0;

    if (StringUtils.isNotBlank(request.getPrompt())) {
      chars += request.getPrompt().length();
    }

    if (StringUtils.isNotBlank(request.getContext())) {
      chars += request.getContext().length();
    }

    if (!CollectionUtils.isEmpty(request.getAttachments())) {
      for (String attachment : request.getAttachments()) {
        chars += attachment.length();
      }
    }

    return chars / props.getCharPerToken();
  }
}
