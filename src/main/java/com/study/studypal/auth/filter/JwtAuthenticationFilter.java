package com.study.studypal.auth.filter;

import com.study.studypal.auth.enums.AccountRole;
import com.study.studypal.auth.exception.AuthErrorCode;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.auth.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
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
    private final CacheManager cacheManager;
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
                String storedAccessToken = cacheManager.getCache(CacheNames.ACCESS_TOKENS).get(CacheKeyUtils.of(userId), String.class);
                if (storedAccessToken == null) {
                    throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
                }

                if(!storedAccessToken.equals(accessToken)) {
                    throw new BaseException(AuthErrorCode.ACCOUNT_LOGGED_IN_ANOTHER_DEVICE);
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
