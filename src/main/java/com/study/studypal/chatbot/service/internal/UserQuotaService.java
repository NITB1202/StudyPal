package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.entity.UserQuota;
import java.util.List;
import java.util.UUID;

public interface UserQuotaService {
  UserQuota getById(UUID id);

  void initializeUsage(UUID userId);

  void validateTokenQuota(String prompt, String context, List<String> attachments);
}
