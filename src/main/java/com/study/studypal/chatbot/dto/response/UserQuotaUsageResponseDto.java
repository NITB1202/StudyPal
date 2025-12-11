package com.study.studypal.chatbot.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuotaUsageResponseDto {
  private Long usedRequests;

  private Long totalRequests;
}
