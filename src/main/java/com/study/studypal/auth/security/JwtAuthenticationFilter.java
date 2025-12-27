package com.study.studypal.auth.security;

import static com.study.studypal.auth.constant.AuthConstant.AUTH_PREFIX;
import static com.study.studypal.auth.constant.AuthConstant.BEARER_PREFIX;
import static com.study.studypal.auth.constant.AuthConstant.ROLE_PREFIX;
import static com.study.studypal.common.util.Constants.AUTHORIZATION_HEADER;

import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.exception.AuthErrorCode;
import com.study.studypal.auth.exception.JwtAuthenticationException;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.util.CacheKeyUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final CacheManager cacheManager;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      String accessToken = authHeader.substring(BEARER_PREFIX.length());

      UUID userId = jwtService.extractId(accessToken);
      AccountRole role = jwtService.extractAccountRole(accessToken);

      String path = request.getRequestURI();
      if (!path.startsWith(AUTH_PREFIX)) {
        Cache cache = cacheManager.getCache(CacheNames.ACCESS_TOKENS);
        if (cache == null) {
          filterChain.doFilter(request, response);
          return;
        }

        String storedAccessToken = cache.get(CacheKeyUtils.of(userId), String.class);
        if (storedAccessToken == null) {
          throw new JwtAuthenticationException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        if (!storedAccessToken.equals(accessToken)) {
          throw new JwtAuthenticationException(AuthErrorCode.ACCOUNT_LOGGED_IN_ANOTHER_DEVICE);
        }
      }

      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(
              userId, null, List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.toString())));

      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}
