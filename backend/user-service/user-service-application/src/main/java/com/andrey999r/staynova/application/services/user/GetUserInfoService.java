package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.queries.GetUserInfoInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.GetUserInfoQuery;
import com.andrey999r.staynova.domain.ports.in.results.UserInfoResult;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GetUserInfoService implements GetUserInfoInputPort {

  private final UserRepository userRepository;

  @Override
  public UserInfoResult execute(GetUserInfoQuery command) {
    log.info("GetUserInfoService execute login={}", command.login());
    User user = userRepository.findByLogin(command.login());
    List<RoleName> roleNames = user.getRoles().stream().map(Role::getRoleName).toList();
    log.info("User info fetched for login={}", command.login());
    return new UserInfoResult(user.getLogin(), user.getPassword(), roleNames);
  }
}
