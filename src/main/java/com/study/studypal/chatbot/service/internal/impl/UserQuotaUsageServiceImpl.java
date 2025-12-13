package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.UserQuotaErrorCode;
import com.study.studypal.chatbot.repository.UserQuotaRepository;
import com.study.studypal.chatbot.service.internal.UserQuotaUsageService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQuotaUsageServiceImpl implements UserQuotaUsageService {
  private final UserQuotaRepository userQuotaRepository;
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
  public void validateTokenQuota(String prompt, String context, List<String> attachments) {}
}
