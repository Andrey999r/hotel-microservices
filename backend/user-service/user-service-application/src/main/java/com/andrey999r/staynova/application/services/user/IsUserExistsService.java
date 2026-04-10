package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.ports.in.queries.IsUserExistsInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.user.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class IsUserExistsService implements IsUserExistsInputPort {

  private final UserRepository userRepository;

  @Override
  public Boolean execute(Login input) {
    log.info("IsUserExistsService execute login={}", input);
    boolean exists = userRepository.existsByLogin(input);
    log.info("IsUserExistsService login={} exists={}", input, exists);
    return exists;
  }
}
