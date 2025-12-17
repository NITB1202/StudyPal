package com.study.studypal.chatbot.client;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
import com.study.studypal.chatbot.exception.ChatbotErrorCode;
import com.study.studypal.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AIRestClient {
  private final RestClient restClient;

  public AIResponseDto ask(AIRequestDto request) {
    return restClient
        .post()
        .uri("/api/ask")
        .body(request)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            (req, res) -> {
              throw new BaseException(ChatbotErrorCode.CHATBOT_INVALID_REQUEST);
            })
        .onStatus(
            HttpStatusCode::is5xxServerError,
            (req, res) -> {
              throw new BaseException(ChatbotErrorCode.CHATBOT_SERVICE_UNAVAILABLE);
            })
        .body(AIResponseDto.class);
  }
}
