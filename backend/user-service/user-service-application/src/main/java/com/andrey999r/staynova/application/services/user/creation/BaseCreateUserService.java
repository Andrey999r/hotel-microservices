package com.andrey999r.staynova.application.services.user.creation;

import com.andrey999r.staynova.application.services.exceptions.UserAlreadyExistsException;
import com.andrey999r.staynova.domain.models.NotificationEventType;
import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.out.NotificationOutputPort;
import com.andrey999r.staynova.domain.ports.out.ObjectStorageOutputPort;
import com.andrey999r.staynova.domain.ports.out.PasswordEncoderOutputPort;
import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.ports.out.requests.NotificationSendRequest;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;
import com.andrey999r.staynova.domain.vo.user.PicturePath;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseCreateUserService {

  protected final UserRepository userRepository;
  protected final RoleRepository roleRepository;
  protected final ObjectStorageOutputPort objectStorageOutputPort;
  protected final PasswordEncoderOutputPort passwordEncoderOutputPort;
  protected final NotificationOutputPort notificationOutputPort;
  protected final RoleTypeOutputPort roleTypeOutputPort;

  protected abstract RoleName resolveRoleName();

  protected void create(Login login, Password password, Email email) {
    log.info("{} create login={}", getClass().getSimpleName(), login);
    if (userRepository.existsByLogin(login)) {
      log.warn("Attempt to register with already existing login: {}", login);
      throw new UserAlreadyExistsException(login);
    }
    Role role = roleRepository.findByRoleName(resolveRoleName());
    Password encodedPassword = passwordEncoderOutputPort.encodePassword(password);
    PicturePath userPicturePath = new PicturePath(objectStorageOutputPort.getDefaultPicturePath());
    User user =
        User.builder()
            .login(login)
            .password(encodedPassword)
            .email(email)
            .roles(Set.of(role))
            .picturePath(userPicturePath)
            .build();
    userRepository.save(user);
    notificationOutputPort.sendNotificationInfo(
        new NotificationSendRequest(login, NotificationEventType.REGISTRATION));
    log.info("{} created successfully: {}", getClass().getSimpleName(), login);
  }
}
