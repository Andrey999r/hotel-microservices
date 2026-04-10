package com.andrey999r.staynova.domain.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.user.BlankLoginException;
import com.andrey999r.staynova.domain.exceptions.user.LoginLengthException;
import com.andrey999r.staynova.domain.exceptions.user.NullLoginException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Login")
class LoginTest {

  @Test
  @DisplayName("Создаёт логин, если значение корректное")
  void constructor_shouldCreateLogin_whenValueIsValid() {
    // arrange & act
    Login login = new Login("johndoe");

    // assert
    assertThat(login.login()).isEqualTo("johndoe");
  }

  @Test
  @DisplayName("Бросает NullLoginException, если значение null")
  void constructor_shouldThrowNullLoginException_whenValueIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Login(null))
        .isInstanceOf(NullLoginException.class);
  }

  @Test
  @DisplayName("Бросает BlankLoginException, если значение состоит только из пробелов")
  void constructor_shouldThrowBlankLoginException_whenValueIsBlank() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Login("   "))
        .isInstanceOf(BlankLoginException.class);
  }

  @Test
  @DisplayName("Бросает BlankLoginException, если значение пустое")
  void constructor_shouldThrowBlankLoginException_whenValueIsEmpty() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Login(""))
        .isInstanceOf(BlankLoginException.class);
  }

  @Test
  @DisplayName("Бросает LoginLengthException, если длина логина меньше минимальной")
  void constructor_shouldThrowLoginLengthException_whenValueIsTooShort() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Login("abc"))
        .isInstanceOf(LoginLengthException.class);
  }

  @Test
  @DisplayName("Создаёт логин ровно минимальной длины")
  void constructor_shouldCreateLogin_whenLengthEqualsMinimum() {
    // arrange
    String value = "a".repeat(Login.MIN_LENGTH);

    // act
    Login login = new Login(value);

    // assert
    assertThat(login.login()).isEqualTo(value);
  }
}
