package com.study.studypal.chatbot.service.api;

import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import java.util.UUID;

public interface UserQuotaUsageService {
  UserQuotaUsageResponseDto getUsage(UUID userId);
}
