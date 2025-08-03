package com.study.studypal.common.security;

import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.common.exception.UnauthorizedException;
import com.study.studypal.common.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Object> redis;
    private static final String AUTH_PREFIX = "/api/auth";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            UUID userId = JwtUtils.extractId(accessToken);
            AccountRole role = JwtUtils.extractAccountRole(accessToken);

            String path = request.getRequestURI();
            if (!path.startsWith(AUTH_PREFIX)) {
                String storedAccessToken = (String) redis.opsForValue().get(JwtUtils.getAccessTokenRedisKey(userId));
                if (storedAccessToken == null) {
                    throw new UnauthorizedException("Invalid or expired token.");
                }

                if(!storedAccessToken.equals(accessToken)) {
                    throw new UnauthorizedException("Account logged in from another device.");
                }
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toString())));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
