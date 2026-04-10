package com.andrey999r.staynova.application.services.role;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.commands.SaveRoleInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SaveRoleService implements SaveRoleInputPort {

  private final RoleRepository roleRepository;

  @Override
  public void execute(Role input) {
    log.info("Saving role: name={}", input.getRoleName());
    roleRepository.save(input);
    log.info("Role saved: name={}", input.getRoleName());
  }
}
