package com.study.studypal.chatbot.client;

import com.study.studypal.chatbot.dto.external.AIRequestDto;
import com.study.studypal.chatbot.dto.external.AIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AIClient {
  private final WebClient webClient;

  public Flux<ServerSentEvent<AIResponseDto>> ask(AIRequestDto request) {
    return webClient
        .post()
        .uri("/api/ask")
        .bodyValue(request)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<>() {});
  }
}
