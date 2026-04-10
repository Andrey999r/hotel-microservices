package com.andrey999r.staynova.application.services.role;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.queries.FindRoleByNameInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FindRoleByNameService implements FindRoleByNameInputPort {
  private final RoleRepository roleRepository;

  @Override
  public Role execute(RoleName input) {
    log.debug("Finding role by name={}", input);
    return roleRepository.findByRoleName(input);
  }
}
