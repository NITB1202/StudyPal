package com.study.studypal.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "chatbot")
@Getter
@Setter
public class ChatbotProperties {
  private long dailyQuotaTokens;
  private long avgTokensPerRequest;
}
