package com.study.studypal.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties(prefix = "chatbot")
@Getter
@Setter
public class ChatbotProperties {
  private Long dailyQuotaTokens;
  private Long avgTokensPerRequest;
  private DataSize maxFileSize;
  private DataSize maxTotalSize;
}
