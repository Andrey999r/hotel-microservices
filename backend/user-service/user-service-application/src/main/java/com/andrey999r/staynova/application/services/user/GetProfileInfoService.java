package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.queries.GetProfileInfoInputPort;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.user.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GetProfileInfoService implements GetProfileInfoInputPort {

  private final UserRepository userRepositoryPort;
  private final AuthOutputPort authOutputPort;

  @Override
  public ProfileInfoResult execute(Void input) {
    Login login = authOutputPort.getUserLogin();
    log.info("GetProfileInfoService execute login={}", login);
    User user = userRepositoryPort.findByLogin(login);
    log.info("Profile fetched for user: {}", login);
    return toResult(user);
  }

  private ProfileInfoResult toResult(User user) {
    return new ProfileInfoResult(
        user.getLogin(), user.getEmail(), user.isEmailSubscriptionStatus(), user.getPicturePath());
  }
}
