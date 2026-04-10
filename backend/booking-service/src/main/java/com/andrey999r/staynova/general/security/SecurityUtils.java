package com.andrey999r.staynova.general.security;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

  public List<String> getRoles() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getAuthorities() == null) {
      return List.of();
    }
    return auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
  }

  public String getUserLogin() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      return null;
    }
    return auth.getPrincipal().toString();
  }
}
