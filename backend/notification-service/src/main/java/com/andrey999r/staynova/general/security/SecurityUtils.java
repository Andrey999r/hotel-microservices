package com.andrey999r.staynova.general.security;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityUtils {

  public List<String> getRoles() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getAuthorities() == null) {
      log.debug("getRoles called with no authentication present");
      return List.of();
    }
    return auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
  }

  public String getUserLogin() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      log.debug("getUserLogin called with no authentication present");
      return null;
    }
    return auth.getPrincipal().toString();
  }
}
