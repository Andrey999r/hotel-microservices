package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.SaveUserInputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SaveUserService implements SaveUserInputPort {

  private final UserRepository userRepository;

  @Override
  public void execute(User input) {
    log.info("SaveUserService execute login={}", input.getLogin());
    userRepository.save(input);
    log.info("SaveUserService saved user login={}", input.getLogin());
  }
}
