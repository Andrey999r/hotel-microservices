package com.andrey999r.staynova.application.services.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.andrey999r.staynova.application.services.exceptions.UserAlreadyExistsException;
import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.RegisterUserCommand;
import com.andrey999r.staynova.domain.ports.out.AdminSecretOutputPort;
import com.andrey999r.staynova.domain.ports.out.NotificationOutputPort;
import com.andrey999r.staynova.domain.ports.out.ObjectStorageOutputPort;
import com.andrey999r.staynova.domain.ports.out.PasswordEncoderOutputPort;
import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserService")
class RegisterUserServiceTest {

  @Mock UserRepository userRepository;
  @Mock RoleRepository roleRepository;
  @Mock ObjectStorageOutputPort objectStorageOutputPort;
  @Mock PasswordEncoderOutputPort passwordEncoderOutputPort;
  @Mock NotificationOutputPort notificationOutputPort;
  @Mock RoleTypeOutputPort roleTypeOutputPort;
  @Mock AdminSecretOutputPort adminSecretOutputPort;

  RegisterUserService registerUserService;

  @BeforeEach
  void setUp() {
    registerUserService =
        new RegisterUserService(
            userRepository,
            roleRepository,
            objectStorageOutputPort,
            passwordEncoderOutputPort,
            notificationOutputPort,
            roleTypeOutputPort,
            adminSecretOutputPort);
  }

  @Test
  @DisplayName("Создаёт обычного пользователя, если секрет не передан")
  void execute_shouldCreateRegularUser_whenSecretNotProvided() {
    // arrange
    Login login = new Login("johndoe");
    Email email = new Email("john@example.com");
    Password password = new Password("Secret123");
    Password encodedPassword = new Password("Encoded1x");
    RoleName userRoleName = new RoleName("ROLE_USER");
    Role userRole = new Role();
    userRole.setRoleName(userRoleName);
    RegisterUserCommand command = new RegisterUserCommand(login, email, password, null);

    Mockito.when(adminSecretOutputPort.getAdminSecret()).thenReturn("admin-secret");
    Mockito.when(userRepository.existsByLogin(login)).thenReturn(false);
    Mockito.when(roleTypeOutputPort.getUserRoleName()).thenReturn(userRoleName);
    Mockito.when(roleRepository.findByRoleName(userRoleName)).thenReturn(userRole);
    Mockito.when(passwordEncoderOutputPort.encodePassword(password)).thenReturn(encodedPassword);
    Mockito.when(objectStorageOutputPort.getDefaultPicturePath())
        .thenReturn("https://cdn.example.com/default.png");

    // act
    registerUserService.execute(command);

    // assert
    Mockito.verify(userRepository).save(any(User.class));
    Mockito.verify(notificationOutputPort).sendNotificationInfo(any());
    Mockito.verify(roleTypeOutputPort).getUserRoleName();
    Mockito.verify(roleTypeOutputPort, Mockito.never()).getAdminRoleName();
  }

  @Test
  @DisplayName("Создаёт администратора, если передан правильный секрет")
  void execute_shouldCreateAdminUser_whenCorrectSecretProvided() {
    // arrange
    Login login = new Login("adminuser");
    Email email = new Email("admin@example.com");
    Password password = new Password("Secret123");
    Password encodedPassword = new Password("Encoded1x");
    RoleName adminRoleName = new RoleName("ROLE_ADMIN");
    Role adminRole = new Role();
    adminRole.setRoleName(adminRoleName);
    RegisterUserCommand command = new RegisterUserCommand(login, email, password, "correct-secret");

    Mockito.when(adminSecretOutputPort.getAdminSecret()).thenReturn("correct-secret");
    Mockito.when(userRepository.existsByLogin(login)).thenReturn(false);
    Mockito.when(roleTypeOutputPort.getAdminRoleName()).thenReturn(adminRoleName);
    Mockito.when(roleRepository.findByRoleName(adminRoleName)).thenReturn(adminRole);
    Mockito.when(passwordEncoderOutputPort.encodePassword(password)).thenReturn(encodedPassword);
    Mockito.when(objectStorageOutputPort.getDefaultPicturePath())
        .thenReturn("https://cdn.example.com/default.png");

    // act
    registerUserService.execute(command);

    // assert
    Mockito.verify(userRepository).save(any(User.class));
    Mockito.verify(notificationOutputPort).sendNotificationInfo(any());
    Mockito.verify(roleTypeOutputPort).getAdminRoleName();
    Mockito.verify(roleTypeOutputPort, Mockito.never()).getUserRoleName();
  }

  @Test
  @DisplayName("Бросает UserAlreadyExistsException, если логин уже занят")
  void execute_shouldThrowUserAlreadyExistsException_whenLoginTaken() {
    // arrange
    Login login = new Login("johndoe");
    Email email = new Email("john@example.com");
    Password password = new Password("Secret123");
    RegisterUserCommand command = new RegisterUserCommand(login, email, password, null);

    Mockito.when(adminSecretOutputPort.getAdminSecret()).thenReturn("admin-secret");
    Mockito.when(userRepository.existsByLogin(login)).thenReturn(true);

    // act & assert
    assertThrows(UserAlreadyExistsException.class, () -> registerUserService.execute(command));

    Mockito.verify(userRepository, Mockito.never()).save(any());
    Mockito.verify(notificationOutputPort, Mockito.never()).sendNotificationInfo(any());
  }

  @Test
  @DisplayName("Создаёт обычного пользователя, если передан неверный секрет")
  void execute_shouldCreateRegularUser_whenWrongSecretProvided() {
    // arrange
    Login login = new Login("johndoe");
    Email email = new Email("john@example.com");
    Password password = new Password("Secret123");
    Password encodedPassword = new Password("Encoded1x");
    RoleName userRoleName = new RoleName("ROLE_USER");
    Role userRole = new Role();
    userRole.setRoleName(userRoleName);
    RegisterUserCommand command = new RegisterUserCommand(login, email, password, "wrong-secret");

    Mockito.when(adminSecretOutputPort.getAdminSecret()).thenReturn("correct-secret");
    Mockito.when(userRepository.existsByLogin(login)).thenReturn(false);
    Mockito.when(roleTypeOutputPort.getUserRoleName()).thenReturn(userRoleName);
    Mockito.when(roleRepository.findByRoleName(userRoleName)).thenReturn(userRole);
    Mockito.when(passwordEncoderOutputPort.encodePassword(password)).thenReturn(encodedPassword);
    Mockito.when(objectStorageOutputPort.getDefaultPicturePath())
        .thenReturn("https://cdn.example.com/default.png");

    // act
    registerUserService.execute(command);

    // assert
    Mockito.verify(userRepository).save(any(User.class));
    Mockito.verify(notificationOutputPort).sendNotificationInfo(any());
    Mockito.verify(roleTypeOutputPort).getUserRoleName();
    Mockito.verify(roleTypeOutputPort, Mockito.never()).getAdminRoleName();
  }
}
