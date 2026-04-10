package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.AddRoleToUserCommand;
import com.andrey999r.staynova.domain.ports.in.commands.AddRoleToUserInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AddRoleToUserService implements AddRoleToUserInputPort {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  public void execute(AddRoleToUserCommand command) {
    log.info("AddRoleToUserService execute login={} name={}", command.login(), command.roleName());
    User user = userRepository.findByLogin(command.login());
    Role role = roleRepository.findByRoleName(command.roleName());
    user.getRoles().add(role);
    userRepository.save(user);
    log.info("Role {} added to user {}", command.roleName(), command.login());
  }
}
