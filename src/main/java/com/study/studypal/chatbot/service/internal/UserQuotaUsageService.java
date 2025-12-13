package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.entity.UserQuota;
import java.util.UUID;

public interface UserQuotaUsageService {
  UserQuota getById(UUID id);

  void initializeUsage(UUID userId);
}
