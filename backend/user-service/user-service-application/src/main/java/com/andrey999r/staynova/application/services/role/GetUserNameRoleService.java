package com.andrey999r.staynova.application.services.role;

import com.andrey999r.staynova.domain.ports.in.queries.GetUserNameRoleInputPort;
import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GetUserNameRoleService implements GetUserNameRoleInputPort {
  private final RoleTypeOutputPort roleTypeOutputPort;

  @Override
  public RoleName execute(Void input) {
    return roleTypeOutputPort.getAdminRoleName();
  }
}
