package com.study.studypal.auth.security;

import static com.study.studypal.auth.constant.AuthConstant.WS_ACCESS_TOKEN_QUERY_PARAM;
import static com.study.studypal.common.util.Constants.WS_USER_ID;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
  private final JwtService jwtService;

  @Override
  public boolean beforeHandshake(
      @NotNull ServerHttpRequest request,
      @NotNull ServerHttpResponse response,
      @NotNull WebSocketHandler wsHandler,
      @NotNull Map<String, Object> attributes) {
    URI uri = request.getURI();
    String accessToken =
        UriComponentsBuilder.fromUri(uri)
            .build()
            .getQueryParams()
            .getFirst(WS_ACCESS_TOKEN_QUERY_PARAM);

    if (StringUtils.isBlank(accessToken)) {
      return false;
    }

    UUID userId = jwtService.extractId(accessToken);
    if (userId == null) {
      return false;
    }

    attributes.put(WS_USER_ID, userId);
    return true;
  }

  @Override
  public void afterHandshake(
      @NotNull ServerHttpRequest request,
      @NotNull ServerHttpResponse response,
      @NotNull WebSocketHandler wsHandler,
      Exception exception) {
    // Will be implemented when needed
  }
}
