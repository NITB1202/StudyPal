package com.study.studypal.auth.security;

import com.study.studypal.auth.enums.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private static final long ACCESS_TOKEN_EXPIRATION_MS = 60 * 60 * 1000L; // 1 hour
  private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L; // 1 week
  private final Key secretKey;

  public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String generateAccessToken(UUID userId, AccountRole role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MS);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(UUID userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MS);

    return Jwts.builder()
        .setSubject(userId.toString())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public UUID extractId(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

    return UUID.fromString(claims.getSubject());
  }

  public AccountRole extractAccountRole(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    return AccountRole.valueOf(claims.get("role", String.class));
  }
}
