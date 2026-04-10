package com.andrey999r.staynova.application.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.UpdateProfileCommand;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
import com.andrey999r.staynova.domain.ports.out.EmailSubscriptionOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.PicturePath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProfileService")
class UpdateProfileServiceTest {

  @Mock UserRepository userRepository;
  @Mock AuthOutputPort authOutputPort;
  @Mock EmailSubscriptionOutputPort emailSubscriptionOutputPort;
  @InjectMocks UpdateProfileService updateProfileService;

  @Test
  @DisplayName("Обновляет email пользователя и сохраняет без отправки подписки")
  void execute_shouldUpdateEmail_whenEmailProvided() {
    // arrange
    Login login = new Login("johndoe");
    Email newEmail = new Email("new@example.com");
    PicturePath picturePath = new PicturePath("https://cdn.example.com/avatar.png");

    User user = new User();
    user.setLogin(login);
    user.setEmail(new Email("old@example.com"));
    user.setEmailSubscriptionStatus(false);
    user.setPicturePath(picturePath);

    Mockito.when(authOutputPort.getUserLogin()).thenReturn(login);
    Mockito.when(userRepository.findByLogin(login)).thenReturn(user);

    UpdateProfileCommand command = new UpdateProfileCommand(newEmail, false);

    // act
    ProfileInfoResult result = updateProfileService.execute(command);

    // assert
    assertEquals(newEmail, result.email());
    Mockito.verify(userRepository).save(user);
    Mockito.verify(emailSubscriptionOutputPort, Mockito.never()).sendSubscriptionInfo(any());
  }

  @Test
  @DisplayName("Отправляет событие подписки, если статус email-уведомлений изменился")
  void execute_shouldSendSubscription_whenNotificationsStatusChanged() {
    // arrange
    Login login = new Login("johndoe");
    Email email = new Email("john@example.com");
    PicturePath picturePath = new PicturePath("https://cdn.example.com/avatar.png");

    User user = new User();
    user.setLogin(login);
    user.setEmail(email);
    user.setEmailSubscriptionStatus(false);
    user.setPicturePath(picturePath);

    Mockito.when(authOutputPort.getUserLogin()).thenReturn(login);
    Mockito.when(userRepository.findByLogin(login)).thenReturn(user);

    UpdateProfileCommand command = new UpdateProfileCommand(null, true);

    // act
    updateProfileService.execute(command);

    // assert
    Mockito.verify(emailSubscriptionOutputPort).sendSubscriptionInfo(any());
    Mockito.verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Не отправляет событие подписки, если статус email-уведомлений не изменился")
  void execute_shouldNotSendSubscription_whenNotificationsStatusUnchanged() {
    // arrange
    Login login = new Login("johndoe");
    Email email = new Email("john@example.com");
    PicturePath picturePath = new PicturePath("https://cdn.example.com/avatar.png");

    User user = new User();
    user.setLogin(login);
    user.setEmail(email);
    user.setEmailSubscriptionStatus(true);
    user.setPicturePath(picturePath);

    Mockito.when(authOutputPort.getUserLogin()).thenReturn(login);
    Mockito.when(userRepository.findByLogin(login)).thenReturn(user);

    UpdateProfileCommand command = new UpdateProfileCommand(null, true);

    // act
    updateProfileService.execute(command);

    // assert
    Mockito.verify(emailSubscriptionOutputPort, Mockito.never()).sendSubscriptionInfo(any());
    Mockito.verify(userRepository).save(user);
  }
}
