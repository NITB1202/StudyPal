package com.study.studypal.chat.service.internal;

import static com.study.studypal.chat.constant.ChatConstant.WS_TEAM_ID;
import static com.study.studypal.common.exception.code.CommonErrorCode.WEBSOCKET_SEND_MESSAGE_FAILED;
import static com.study.studypal.common.util.Constants.WS_USER_ID;

import com.study.studypal.chat.dto.internal.ConnectedUser;
import com.study.studypal.chat.dto.internal.WebSocketChatMessage;
import com.study.studypal.chat.enums.ChatEventType;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.common.util.JsonUtils;
import com.study.studypal.common.util.WebsocketUtils;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
  private final Map<WebSocketSession, ConnectedUser> sessionUserMap = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(@NotNull WebSocketSession session) {
    UUID userId = (UUID) session.getAttributes().get(WS_USER_ID);

    String teamIdStr = WebsocketUtils.extractFromQueryParam(session, WS_TEAM_ID);
    UUID teamId = StringUtils.isNotBlank(teamIdStr) ? UUID.fromString(teamIdStr) : null;

    if (userId != null && teamId != null) {
      sessionUserMap.put(session, new ConnectedUser(userId, teamId));
      log.info("User {} connected to team {}", userId, teamId);
    } else {
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException e) {
        throw new BaseException(CommonErrorCode.WEBSOCKET_CONNECT_FAILED, e.getMessage());
      }
    }
  }

  @Override
  public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
    log.info("Received: {}", message.getPayload());
  }

  @Override
  public void afterConnectionClosed(
      @NotNull WebSocketSession session, @NotNull CloseStatus status) {
    ConnectedUser connectedUser = sessionUserMap.get(session);
    log.info(
        "User {} disconnected from team {}", connectedUser.getUserId(), connectedUser.getTeamId());
    sessionUserMap.remove(session);
  }

  public void sendMessageToOnlineMembers(UUID teamId, ChatEventType type, Object data) {
    WebSocketChatMessage message = new WebSocketChatMessage(type, data);
    String payload = JsonUtils.serialize(message);
    sessionUserMap.forEach(
        (session, user) -> {
          if (user.getTeamId().equals(teamId) && session.isOpen()) {
            try {
              session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
              throw new BaseException(WEBSOCKET_SEND_MESSAGE_FAILED, e.getMessage());
            }
          }
        });
  }

  public boolean isUserInTeam(UUID userId, UUID teamId) {
    return sessionUserMap.values().stream()
        .anyMatch(u -> u.getUserId().equals(userId) && u.getTeamId().equals(teamId));
  }
}
