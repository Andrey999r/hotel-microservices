package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.queries.FindAllUsersByRoleNameInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.FindAllUsersByRoleNameQuery;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FindAllUsersByRoleNameService implements FindAllUsersByRoleNameInputPort {

  private final UserRepository userRepository;

  @Override
  public List<User> execute(FindAllUsersByRoleNameQuery command) {
    log.info(
        "FindAllUsersByRoleNameService execute name={} page={} size={}",
        command.roleName(),
        command.filter().page(),
        command.filter().size());
    Filtered<User> filteredUsers =
        userRepository.findAllByRoleName(command.filter(), command.roleName());
    log.info("FindAllUsersByRoleNameService found {} users", filteredUsers.content().size());
    return filteredUsers.content();
  }
}
