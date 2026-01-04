package com.study.studypal.auth.security;

import static com.study.studypal.auth.constant.AuthConstant.ROLE_CLAIM;

import com.study.studypal.auth.config.JwtProperties;
import com.study.studypal.auth.enums.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private final JwtProperties jwtProperties;
  private final Key secretKey;

  public JwtService(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
  }

  public String generateAccessToken(UUID userId, AccountRole role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim(ROLE_CLAIM, role)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(UUID userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationMs());

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
    return AccountRole.valueOf(claims.get(ROLE_CLAIM, String.class));
  }
}
