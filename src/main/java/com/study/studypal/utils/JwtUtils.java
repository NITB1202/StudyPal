package com.study.studypal.utils;

import com.study.studypal.enums.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

public class JwtUtils {
    private static final String keyStr = "mZf8Uhq7n3rH0kL3iU5Q1yLvBrOtFvXcRgxO2b7nM8U=";
    private static final Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyStr));
    private static final long accessTokenExpirationMs = 60 * 60 * 1000; //1h
    private static final long refreshTokenExpirationMs = 7 * 24 * 60 * 60 * 1000L; //1w

    public static String generateAccessToken(UUID userId, AccountRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(UUID accountId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(accountId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getAccessTokenRedisKey(UUID userId) {
        return "access_token_" + userId.toString();
    }

    public static String getRefreshTokenRedisKey(UUID accountId) {
        return "refresh_token_" + accountId.toString();
    }

    public static Duration getAccessTokenTTL() {
        return Duration.ofMillis(accessTokenExpirationMs);
    }

    public static Duration getRefreshTokenTTL() {
        return Duration.ofMillis(refreshTokenExpirationMs);
    }

    public static UUID extractId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return UUID.fromString(claims.getSubject());
    }

    public static AccountRole extractAccountRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return AccountRole.valueOf(claims.get("role", String.class));
    }
}
