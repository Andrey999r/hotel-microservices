package com.andrey999r.staynova.infrastructure.config.ports;

import com.andrey999r.staynova.application.services.role.*;
import com.andrey999r.staynova.application.services.user.*;
import com.andrey999r.staynova.domain.ports.in.commands.*;
import com.andrey999r.staynova.domain.ports.in.queries.*;
import com.andrey999r.staynova.domain.ports.out.*;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InputPortsConfiguration {

  @Bean
  ExistsByRoleNameInputPort existsByRoleNameInputPort(RoleRepository roleRepository) {
    return new ExistsByRoleNameService(roleRepository);
  }

  @Bean
  FindAllRolesInputPort findAllRolesInputPort(RoleRepository roleRepository) {
    return new FindAllRolesService(roleRepository);
  }

  @Bean
  FindRoleByNameInputPort findRoleByNameInputPort(RoleRepository roleRepository) {
    return new FindRoleByNameService(roleRepository);
  }

  @Bean
  GetAdminRoleNameInputPort getAdminRoleInputPort(RoleTypeOutputPort roleTypeOutputPort) {
    return new GetAdminRoleNameService(roleTypeOutputPort);
  }

  @Bean
  GetUserNameRoleInputPort getUserRoleInputPort(RoleTypeOutputPort roleTypeOutputPort) {
    return new GetUserNameRoleService(roleTypeOutputPort);
  }

  @Bean
  SaveRoleInputPort saveRoleInputPort(RoleRepository roleRepository) {
    return new SaveRoleService(roleRepository);
  }

  @Bean
  AddRoleToUserInputPort addRoleToUserInputPort(
      RoleRepository roleRepository, UserRepository userRepository) {
    return new AddRoleToUserService(userRepository, roleRepository);
  }

  @Bean
  FindAllUsersByRoleNameInputPort findAllUsersByRoleNameInputPort(UserRepository userRepository) {
    return new FindAllUsersByRoleNameService(userRepository);
  }

  @Bean
  FindUserByLoginInputPort findUserByLoginInputPort(UserRepository userRepository) {
    return new FindUserByLoginService(userRepository);
  }

  @Bean
  GetProfileInfoInputPort getProfileInfoInputPort(
      UserRepository userRepository, AuthOutputPort authOutputPort) {
    return new GetProfileInfoService(userRepository, authOutputPort);
  }

  @Bean
  GetUserInfoInputPort getUserInfoInputPort(UserRepository userRepository) {
    return new GetUserInfoService(userRepository);
  }

  @Bean
  IsUserExistsInputPort isUserExistsInputPort(UserRepository userRepository) {
    return new IsUserExistsService(userRepository);
  }

  @Bean
  SaveUserInputPort saveUserInputPort(UserRepository userRepository) {
    return new SaveUserService(userRepository);
  }

  @Bean
  UpdateProfileInputPort updateProfileInputPort(
      UserRepository userRepository,
      AuthOutputPort authOutputPort,
      EmailSubscriptionOutputPort emailSubscriptionOutputPort) {
    return new UpdateProfileService(userRepository, authOutputPort, emailSubscriptionOutputPort);
  }

  @Bean
  UploadPhotoInputPort uploadPhotoInputPort(
      UserRepository userRepositoryPort,
      AuthOutputPort authOutputPort,
      ObjectStorageOutputPort objectStorageOutputPort) {
    return new UploadPhotoService(userRepositoryPort, authOutputPort, objectStorageOutputPort);
  }

  @Bean
  RegisterUserInputPort registerUserInputPort(
      UserRepository userRepository,
      RoleRepository roleRepository,
      ObjectStorageOutputPort objectStorageOutputPort,
      PasswordEncoderOutputPort passwordEncoderOutputPort,
      NotificationOutputPort notificationOutputPort,
      RoleTypeOutputPort roleTypeOutputPort,
      AdminSecretOutputPort adminSecretOutputPort) {
    return new RegisterUserService(
        userRepository,
        roleRepository,
        objectStorageOutputPort,
        passwordEncoderOutputPort,
        notificationOutputPort,
        roleTypeOutputPort,
        adminSecretOutputPort);
  }
}
