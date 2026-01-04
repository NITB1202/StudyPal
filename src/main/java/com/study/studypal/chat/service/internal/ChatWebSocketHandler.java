package com.study.studypal.chat.service.internal;

import static com.study.studypal.chat.constant.ChatConstant.WS_TEAM_ID_QUERY_PARAM;
import static com.study.studypal.common.util.Constants.WS_USER_ID;

import com.study.studypal.chat.dto.internal.ConnectedMember;
import com.study.studypal.chat.dto.internal.WebSocketChatMessage;
import com.study.studypal.chat.enums.ChatEventType;
import com.study.studypal.common.util.JsonUtils;
import com.study.studypal.common.util.WebsocketUtils;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
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
  private final TeamMembershipInternalService memberService;
  private final Map<WebSocketSession, ConnectedMember> sessionMap = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(@NotNull WebSocketSession session) {
    UUID userId = (UUID) session.getAttributes().get(WS_USER_ID);

    String teamIdStr = WebsocketUtils.extractFromQueryParam(session, WS_TEAM_ID_QUERY_PARAM);
    UUID teamId = StringUtils.isNotBlank(teamIdStr) ? UUID.fromString(teamIdStr) : null;

    if (userId == null || teamId == null) {
      log.error("Connection closed because userId or teamId is missing.");
      WebsocketUtils.closeSession(session);
    }

    try {
      memberService.validateUserBelongsToTeam(userId, teamId);
      sessionMap.put(session, new ConnectedMember(userId, teamId));
      log.info("User {} connected to team {}", userId, teamId);
    } catch (Exception ex) {
      log.error("Connection closed with error: {}", ex.getMessage());
      WebsocketUtils.closeSession(session);
    }
  }

  @Override
  public void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
    log.info("Received: {}", message.getPayload());
  }

  @Override
  public void afterConnectionClosed(
      @NotNull WebSocketSession session, @NotNull CloseStatus status) {
    ConnectedMember connectedMember = sessionMap.get(session);
    log.info(
        "User {} disconnected from team {}",
        connectedMember.getUserId(),
        connectedMember.getTeamId());
    sessionMap.remove(session);
  }

  public void sendMessageToOnlineMembers(UUID teamId, ChatEventType type, Object data) {
    WebSocketChatMessage message = new WebSocketChatMessage(type, data);
    String payload = JsonUtils.serialize(message);
    sessionMap.forEach(
        (session, user) -> {
          if (user.getTeamId().equals(teamId) && session.isOpen()) {
            try {
              session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
              log.error("Send WebSocket message failed with error: {}", e.getMessage());
            }
          }
        });
  }

  public boolean isUserInTeam(UUID userId, UUID teamId) {
    return sessionMap.values().stream()
        .anyMatch(u -> u.getUserId().equals(userId) && u.getTeamId().equals(teamId));
  }
}
