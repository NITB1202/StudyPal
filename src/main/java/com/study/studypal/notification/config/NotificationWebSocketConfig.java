package com.study.studypal.notification.config;

import com.study.studypal.auth.security.WebSocketAuthInterceptor;
import com.study.studypal.notification.service.internal.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class NotificationWebSocketConfig implements WebSocketConfigurer {
  private final WebSocketAuthInterceptor authInterceptor;
  private final NotificationWebSocketHandler notificationWebSocketHandler;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(notificationWebSocketHandler, "/ws/notification")
        .addInterceptors(authInterceptor)
        .setAllowedOrigins("*");
  }
}
