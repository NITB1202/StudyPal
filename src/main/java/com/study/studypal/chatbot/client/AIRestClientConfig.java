package com.study.studypal.chatbot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AIRestClientConfig {
  @Value("${AI_SERVICE_URL}")
  private String baseUrl;

  @Bean
  public RestClient aiRestClient(RestClient.Builder builder) {
    return builder.baseUrl(baseUrl).build();
  }
}
