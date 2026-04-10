package com.andrey999r.staynova.filters;


import com.andrey999r.staynova.exception.BaseException;
import com.andrey999r.staynova.exception.custom.BlacklistedTokenException;
import com.andrey999r.staynova.exception.custom.TokenExpiredException;
import java.util.List;
import java.util.stream.Collectors;

import com.andrey999r.staynova.services.BlacklistService;
import com.andrey999r.staynova.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;
    private final BlacklistService blacklistService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getPath().value();
    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.debug("No Bearer token on request: {}", path);
      return chain.filter(exchange);
    }

    String token = authHeader.substring(7);

    return blacklistService
        .isBlacklisted(token)
        .flatMap(
            blacklisted -> {
              if (blacklisted) {
                log.warn("Blacklisted token on request: {}", path);
                return Mono.error(new BlacklistedTokenException());
              }

              try {
                if (jwtService.isExpired(token)) {
                  log.warn("Expired token on request: {}", path);
                  return Mono.error(new TokenExpiredException());
                }

                String login = jwtService.getLogin(token);
                List<String> roles = jwtService.getRoles(token);

                if (login == null) {
                  log.debug("Token has no subject (login=null), passing through: {}", path);
                  return chain.filter(exchange);
                }

                log.debug("Authenticated request: path={}, login={}, roles={}", path, login, roles);

                var authorities =
                    roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                Authentication authToken =
                    new UsernamePasswordAuthenticationToken(login, null, authorities);

                ServerWebExchange mutatedExchange =
                    exchange
                        .mutate()
                        .request(
                            r ->
                                r.headers(
                                    headers -> {
                                      headers.remove("X-User-Login");
                                      headers.remove("X-User-Roles");
                                      headers.add("X-User-Login", login);
                                      headers.add("X-User-Roles", String.join(",", roles));
                                    }))
                        .build();

                return chain
                    .filter(mutatedExchange)
                    .contextWrite(
                        ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(new SecurityContextImpl(authToken))));
              } catch (BaseException e) {
                log.warn(
                    "JWT validation failed on {}: [{}] {}",
                    path,
                    e.getClass().getSimpleName(),
                    e.getMessage());
                return Mono.error(e);
              }
            });
  }
}
