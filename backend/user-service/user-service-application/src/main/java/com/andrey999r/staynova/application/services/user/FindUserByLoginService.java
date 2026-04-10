package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.queries.FindUserByLoginInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.user.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FindUserByLoginService implements FindUserByLoginInputPort {

  private final UserRepository userRepository;

  @Override
  public User execute(Login input) {
    log.info("FindUserByLoginService execute login={}", input);
    User user = userRepository.findByLogin(input);
    log.info("FindUserByLoginService found user login={}", input);
    return user;
  }
}
