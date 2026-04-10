package com.andrey999r.staynova.infrastructure.adapters.out;

import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.infrastructure.config.RoleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleTypeOutputAdapter implements RoleTypeOutputPort {
  private final RoleConfig roleConfig;

  @Override
  public RoleName getAdminRoleName() {
    return roleConfig.getAdminRoleName();
  }

  @Override
  public RoleName getUserRoleName() {
    return roleConfig.getUserRoleName();
  }
}
