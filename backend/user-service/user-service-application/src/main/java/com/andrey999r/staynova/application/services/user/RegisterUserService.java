package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.application.services.user.creation.BaseCreateUserService;
import com.andrey999r.staynova.domain.ports.in.commands.RegisterUserCommand;
import com.andrey999r.staynova.domain.ports.in.commands.RegisterUserInputPort;
import com.andrey999r.staynova.domain.ports.out.AdminSecretOutputPort;
import com.andrey999r.staynova.domain.ports.out.NotificationOutputPort;
import com.andrey999r.staynova.domain.ports.out.ObjectStorageOutputPort;
import com.andrey999r.staynova.domain.ports.out.PasswordEncoderOutputPort;
import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegisterUserService extends BaseCreateUserService implements RegisterUserInputPort {

  private final AdminSecretOutputPort adminSecretOutputPort;
  private boolean isAdmin;

  public RegisterUserService(
      UserRepository userRepository,
      RoleRepository roleRepository,
      ObjectStorageOutputPort objectStorageOutputPort,
      PasswordEncoderOutputPort passwordEncoderOutputPort,
      NotificationOutputPort notificationOutputPort,
      RoleTypeOutputPort roleTypeOutputPort,
      AdminSecretOutputPort adminSecretOutputPort) {
    super(
        userRepository,
        roleRepository,
        objectStorageOutputPort,
        passwordEncoderOutputPort,
        notificationOutputPort,
        roleTypeOutputPort);
    this.adminSecretOutputPort = adminSecretOutputPort;
  }

  @Override
  protected RoleName resolveRoleName() {
    return isAdmin ? roleTypeOutputPort.getAdminRoleName() : roleTypeOutputPort.getUserRoleName();
  }

  @Override
  public void execute(RegisterUserCommand command) {
    log.info("RegisterUserService execute login={}", command.login());
    isAdmin = adminSecretOutputPort.getAdminSecret().equals(command.adminSecret());
    create(command.login(), command.password(), command.email());
  }
}
