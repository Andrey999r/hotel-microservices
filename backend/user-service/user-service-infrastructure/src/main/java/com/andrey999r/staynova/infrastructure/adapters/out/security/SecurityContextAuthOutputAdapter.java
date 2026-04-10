package com.andrey999r.staynova.infrastructure.adapters.out.security;

import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.securitystarter.services.ContextService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityContextAuthOutputAdapter implements AuthOutputPort {
  private final ContextService contextService;

  @Override
  public List<RoleName> getUserRoleNames() {
    var roleNames = contextService.getUserRoleNames();
    return roleNames.stream().map(RoleName::new).toList();
  }

  @Override
  public Login getUserLogin() {
    var login = contextService.getUserLogin();
    return new Login(login);
  }
}
