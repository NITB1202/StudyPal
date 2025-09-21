package com.study.studypal.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    long startTime = System.currentTimeMillis();

    String method = request.getMethod();
    String uri = request.getRequestURI();

    log.info("Incoming request: {} {}", method, uri);

    filterChain.doFilter(request, response);
    long duration = System.currentTimeMillis() - startTime;

    log.info("Completed: {} {} -> {} ({} ms)", method, uri, response.getStatus(), duration);
  }
}
