package com.study.studypal.auth.security;

import static com.study.studypal.common.util.Constants.DATE_PATTERN;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));

    String json =
        "{"
            + "\"statusCode\": \"401\","
            + "\"errorCode\": \"UNAUTHORIZED\","
            + "\"message\": \""
            + authException.getMessage()
            + "\","
            + "\"timestamp\": \""
            + timestamp
            + "\""
            + "}";

    response.getWriter().write(json);
  }
}
