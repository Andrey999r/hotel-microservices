package com.andrey999r.staynova.securitystarter.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class RoleHeaderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String rolesHeader = request.getHeader("X-User-Roles");
    String login = request.getHeader("X-User-Login");

    if (rolesHeader != null && !rolesHeader.isBlank()) {
      List<SimpleGrantedAuthority> authorities =
          Arrays.stream(rolesHeader.split(","))
              .map(String::trim)
              .filter(role -> !role.isEmpty())
              .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
              .map(SimpleGrantedAuthority::new)
              .toList();

      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(login, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);
      log.debug("Authenticated from headers: login={}, roles={}", login, authorities);
    }

    filterChain.doFilter(request, response);
  }
}
