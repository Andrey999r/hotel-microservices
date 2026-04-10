package com.andrey999r.staynova.application.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
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
@DisplayName("GetProfileInfoService")
class GetProfileInfoServiceTest {

  @Mock UserRepository userRepository;
  @Mock AuthOutputPort authOutputPort;
  @InjectMocks GetProfileInfoService getProfileInfoService;

  @Test
  @DisplayName("Возвращает профиль текущего авторизованного пользователя")
  void execute_shouldReturnProfileInfoForCurrentUser() {
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

    // act
    ProfileInfoResult result = getProfileInfoService.execute(null);

    // assert
    assertEquals(login, result.login());
    assertEquals(email, result.email());
    assertTrue(result.emailNotificationsEnabled());
    assertEquals(picturePath, result.picturePath());
    Mockito.verify(authOutputPort).getUserLogin();
    Mockito.verify(userRepository).findByLogin(login);
  }
}
