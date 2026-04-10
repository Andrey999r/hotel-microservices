package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.UpdateProfileCommand;
import com.andrey999r.staynova.domain.ports.in.commands.UpdateProfileInputPort;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
import com.andrey999r.staynova.domain.ports.out.EmailSubscriptionOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.ports.out.requests.EmailSubscriptionRequest;
import com.andrey999r.staynova.domain.vo.user.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UpdateProfileService implements UpdateProfileInputPort {

  private final UserRepository userRepositoryPort;
  private final AuthOutputPort authOutputPort;
  private final EmailSubscriptionOutputPort emailSubscriptionOutputPort;

  @Override
  public ProfileInfoResult execute(UpdateProfileCommand command) {
    Login login = authOutputPort.getUserLogin();
    log.info("UpdateProfileService execute login={}, command={}", login, command);
    User user = userRepositoryPort.findByLogin(login);

    if (command.email() != null) {
      log.debug("Updating email for user: {}", login);
      user.setEmail(command.email());
    }
    if (user.isEmailSubscriptionStatus() != command.emailSubscriptionStatus()) {
      log.debug(
          "Updating email notifications for user: {}, enabled={}",
          login,
          command.emailSubscriptionStatus());
      user.setEmailSubscriptionStatus(command.emailSubscriptionStatus());
      EmailSubscriptionRequest emailSubscriptionRequest =
          new EmailSubscriptionRequest(login, user.getEmail(), command.emailSubscriptionStatus());
      emailSubscriptionOutputPort.sendSubscriptionInfo(emailSubscriptionRequest);
    }
    userRepositoryPort.save(user);
    log.info("Profile updated successfully for user: {}", login);
    return toResult(user);
  }

  private ProfileInfoResult toResult(User user) {
    return new ProfileInfoResult(
        user.getLogin(), user.getEmail(), user.isEmailSubscriptionStatus(), user.getPicturePath());
  }
}
