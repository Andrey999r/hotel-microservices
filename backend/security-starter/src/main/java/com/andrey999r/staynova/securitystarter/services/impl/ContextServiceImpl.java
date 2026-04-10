package com.andrey999r.staynova.securitystarter.services.impl;

import com.andrey999r.staynova.securitystarter.services.ContextService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class ContextServiceImpl implements ContextService {
  @Override
  public List<String> getUserRoleNames() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) throw new SecurityException("No authentication");
    return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
  }

  @Override
  public String getUserLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null)
      throw new SecurityException("No authentication");
    return auth.getPrincipal().toString();
  }
}
