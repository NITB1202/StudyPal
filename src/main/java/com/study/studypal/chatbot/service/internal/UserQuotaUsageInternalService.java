package com.study.studypal.chatbot.service.internal;

import java.util.UUID;

public interface UserQuotaUsageInternalService {
  void initializeUsage(UUID userId);
}
