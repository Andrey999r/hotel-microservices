package com.andrey999r.staynova.config.jwt;

import com.andrey999r.staynova.exceptions.custom.auth.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtTokenUtils {

  JwtProperties jwtProperties;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UserDetails userDetails) {
    var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    var now = new Date();
    var expiredAt = new Date(now.getTime() + jwtProperties.getExpiration().toMillis());

    log.debug("Generating token for user: {}", userDetails.getUsername());
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiredAt)
        .signWith(getKey())
        .compact();
  }

  public long getExpiration(String token) {
    return getClaims(token).getExpiration().getTime();
  }

  public Claims getClaims(String token) {
    try {
      return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      log.debug("Token expired: {}", e.getMessage());
      return e.getClaims();
    } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      log.warn("Malformed token: {}", e.getMessage());
      throw new InvalidCredentialsException();
    } catch (SignatureException e) {
      log.warn("Invalid token signature: {}", e.getMessage());
      throw new InvalidCredentialsException();
    } catch (JwtException e) {
      log.warn("JWT error: {}", e.getMessage());
      throw new InvalidCredentialsException();
    }
  }
}
