package com.study.studypal.notification.service.internal;

import static com.study.studypal.common.util.Constants.WS_USER_ID;

import com.study.studypal.common.util.JsonUtils;
import com.study.studypal.common.util.WebsocketUtils;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {
  private final Map<WebSocketSession, UUID> sessionUserMap = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(@NotNull WebSocketSession session) {
    UUID userId = (UUID) session.getAttributes().get(WS_USER_ID);
    if (userId == null) {
      log.error("Connection closed because userId is missing.");
      WebsocketUtils.closeSession(session);
    }

    sessionUserMap.put(session, userId);
    log.info("User {} connected to the notification WebSocket", userId);
  }

  @Override
  public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
    log.info("Received: {}", message.getPayload());
  }

  @Override
  public void afterConnectionClosed(
      @NotNull WebSocketSession session, @NotNull CloseStatus status) {
    UUID userId = sessionUserMap.get(session);
    log.info("User {} disconnected from the notification WebSocket", userId);
    sessionUserMap.remove(session);
  }

  public void sendNotificationToOnlineUsers(List<UUID> recipients, NotificationTemplate template) {
    String notification = JsonUtils.serialize(template);
    sessionUserMap.forEach(
        (session, user) -> {
          if (recipients.contains(user) && session.isOpen()) {
            try {
              session.sendMessage(new TextMessage(notification));
            } catch (IOException e) {
              log.error("Send WebSocket message failed with error: {}", e.getMessage());
            }
          }
        });
  }
}
