package com.andrey999r.staynova.application.services.role;

import com.andrey999r.staynova.domain.ports.in.queries.ExistsByRoleNameInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExistsByRoleNameService implements ExistsByRoleNameInputPort {
  private final RoleRepository roleRepository;

  @Override
  public Boolean execute(RoleName input) {
    return roleRepository.existsByRoleName(input);
  }
}
