package com.study.studypal.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebsocketUtils {

  public static String extractFromQueryParam(WebSocketSession session, String key) {
    String query =
        Optional.ofNullable(session).map(WebSocketSession::getUri).map(URI::getQuery).orElse(null);
    if (StringUtils.isBlank(query)) {
      return null;
    }

    for (String param : query.split("&")) {
      String[] pair = param.split("=", 2);
      if (pair.length == 2 && pair[0].equals(key)) {
        return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
      }
    }

    return null;
  }

  public static void closeSession(WebSocketSession session) {
    try {
      session.close(CloseStatus.NOT_ACCEPTABLE);
    } catch (IOException e) {
      log.error("Failed to close WebSocket session", e);
    }
  }
}
