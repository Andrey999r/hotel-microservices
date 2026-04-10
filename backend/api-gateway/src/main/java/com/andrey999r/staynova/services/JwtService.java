package com.andrey999r.staynova.services;

import com.andrey999r.staynova.exception.custom.InvalidTokenException;
import com.andrey999r.staynova.exception.custom.MalformedTokenException;
import com.andrey999r.staynova.exception.custom.TokenExpiredException;
import com.andrey999r.staynova.properties.JwtProperties;

import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

  private final JwtProperties jwtProperties;

  public SecretKey getKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
  }

  public String generateToken(String subject, List<String> roles) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + jwtProperties.getExpiration().toMillis());
    return Jwts.builder()
        .subject(subject)
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiredAt)
        .signWith(getKey())
        .compact();
  }

  public Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      log.debug("JWT expired: {}", e.getMessage());
      throw new TokenExpiredException();
    } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      log.warn("JWT malformed: {}", e.getMessage());
      throw new MalformedTokenException();
    } catch (SignatureException e) {
      log.warn("JWT signature invalid: {}", e.getMessage());
      throw new InvalidTokenException();
    } catch (JwtException e) {
      log.warn("JWT error: {}", e.getMessage());
      throw new MalformedTokenException();
    }
  }

  public String getLogin(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  public List<String> getRoles(String token) {
    return getClaimsFromToken(token).get("roles", List.class);
  }

  public long getExpiration(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getKey())
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getExpiration()
          .getTime();
    } catch (ExpiredJwtException e) {
      return e.getClaims().getExpiration().getTime();
    }
  }

  public boolean isExpired(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      log.warn("JWT malformed during expiry check: {}", e.getMessage());
      throw new MalformedTokenException();
    } catch (SignatureException e) {
      log.warn("JWT invalid signature during expiry check: {}", e.getMessage());
      throw new InvalidTokenException();
    } catch (JwtException e) {
      log.warn("JWT error during expiry check: {}", e.getMessage());
      throw new MalformedTokenException();
    }
  }
}
