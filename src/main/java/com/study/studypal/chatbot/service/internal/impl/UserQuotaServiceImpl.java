package com.study.studypal.chatbot.service.internal.impl;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
  @Transactional
  public void validateAndSetMaxOutputTokens(UUID userId, AIRequestDto request) {
    UserQuota userQuota = getById(userId);

    long estimatedInputTokens = estimateToken(request);
    long totalTokensAfterRequest = userQuota.getUsedQuota() + estimatedInputTokens;

    if (totalTokensAfterRequest > userQuota.getDailyQuota()) {
      throw new BaseException(UserQuotaErrorCode.INSUFFICIENT_QUOTA);
    }

    long remainingTokens = userQuota.getDailyQuota() - totalTokensAfterRequest;
    request.setMaxOutputTokens(remainingTokens);
  }

  @Override
  @Transactional
  public void saveMessageUsage(ChatMessage message, AIResponseDto response, long duration) {
    long inputTokens = response.getInputTokens();
    long outputTokens = response.getOutputTokens();

    MessageUsage messageUsage =
        MessageUsage.builder()
            .message(message)
            .inputTokens(inputTokens)
            .outputTokens(outputTokens)
            .latencyMs(duration)
            .build();

    messageUsageRepository.save(messageUsage);

    UUID userId = message.getUser().getId();
    long totalTokens = inputTokens + outputTokens;

    UserQuota userQuota = getById(userId);
    userQuota.setUsedQuota(userQuota.getUsedQuota() + totalTokens);

    userQuotaRepository.save(userQuota);
  }

  @Override
  public void resetDailyQuotaForAllUsers() {
    List<UserQuota> quotas = userQuotaRepository.findAll();
    for (UserQuota quota : quotas) {
      quota.setUsedQuota(0L);
    }
    userQuotaRepository.saveAll(quotas);
  }

  private long estimateToken(AIRequestDto request) {
    StringBuilder combined = new StringBuilder();

    if (StringUtils.isNotBlank(request.getPrompt())) {
      combined.append(request.getPrompt()).append(" ");
    }

    if (StringUtils.isNotBlank(request.getContext())) {
      combined.append(request.getContext()).append(" ");
    }

    if (!CollectionUtils.isEmpty(request.getAttachments())) {
      request.getAttachments().stream()
          .filter(a -> StringUtils.isNotBlank(a.getContent()))
          .forEach(a -> combined.append(a.getContent()).append(" "));
    }

    EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    Encoding tokenizer = registry.getEncoding(EncodingType.CL100K_BASE);

    return tokenizer.encode(combined.toString()).size();
  }
}
