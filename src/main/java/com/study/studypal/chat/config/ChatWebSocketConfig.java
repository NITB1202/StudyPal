package com.study.studypal.chat.config;

import com.study.studypal.auth.security.WebSocketAuthInterceptor;
import com.study.studypal.chat.service.internal.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ChatWebSocketConfig implements WebSocketConfigurer {
  private final WebSocketAuthInterceptor authInterceptor;
  private final ChatWebSocketHandler chatWebSocketHandler;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(chatWebSocketHandler, "/ws/chat")
        .addInterceptors(authInterceptor)
        .setAllowedOrigins("*");
  }
}
