package com.study.studypal.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startTime = System.currentTimeMillis();
    String method = request.getMethod();
    String uri = request.getRequestURI();

    try {
      String traceId = request.getHeader("X-Trace-Id");
      if (traceId == null || traceId.isBlank()) {
        traceId = UUID.randomUUID().toString();
      }

      String subjectId = request.getHeader("X-Subject-Id");
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        subjectId = authentication.getName();
      }

      MDC.put("traceId", traceId);
      MDC.put("subjectId", subjectId != null ? subjectId : "anonymous");

      log.info("Incoming request: {} {}", method, uri);

      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      log.info("Completed: {} {} -> {} ({} ms)", method, uri, response.getStatus(), duration);

      MDC.clear();
    }
  }
}
