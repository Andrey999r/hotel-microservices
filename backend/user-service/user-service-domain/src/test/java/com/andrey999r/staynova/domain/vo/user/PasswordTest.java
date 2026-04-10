package com.andrey999r.staynova.domain.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.user.BlankPasswordException;
import com.andrey999r.staynova.domain.exceptions.user.NullPasswordException;
import com.andrey999r.staynova.domain.exceptions.user.PasswordLengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Password")
class PasswordTest {

  @Test
  @DisplayName("Создаёт пароль, если значение корректное")
  void constructor_shouldCreatePassword_whenValueIsValid() {
    // arrange & act
    Password password = new Password("secret123");

    // assert
    assertThat(password.password()).isEqualTo("secret123");
  }

  @Test
  @DisplayName("Создаёт пароль из закодированного bcrypt хеша")
  void constructor_shouldCreatePassword_whenValueIsBcryptHash() {
    // arrange
    String bcrypt = "$2a$10$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012345";

    // act
    Password password = new Password(bcrypt);

    // assert
    assertThat(password.password()).isEqualTo(bcrypt);
  }

  @Test
  @DisplayName("Бросает NullPasswordException, если значение null")
  void constructor_shouldThrowNullPasswordException_whenValueIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Password(null))
        .isInstanceOf(NullPasswordException.class);
  }

  @Test
  @DisplayName("Бросает BlankPasswordException, если значение пустое")
  void constructor_shouldThrowBlankPasswordException_whenValueIsEmpty() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Password(""))
        .isInstanceOf(BlankPasswordException.class);
  }

  @Test
  @DisplayName("Бросает BlankPasswordException, если значение состоит из пробелов")
  void constructor_shouldThrowBlankPasswordException_whenValueIsBlank() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Password("   "))
        .isInstanceOf(BlankPasswordException.class);
  }

  @Test
  @DisplayName("Бросает PasswordLengthException, если пароль короче минимальной длины")
  void constructor_shouldThrowPasswordLengthException_whenValueIsTooShort() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Password("short"))
        .isInstanceOf(PasswordLengthException.class);
  }

  @Test
  @DisplayName("Создаёт пароль ровно минимальной длины")
  void constructor_shouldCreatePassword_whenLengthEqualsMinimum() {
    // arrange
    String value = "a".repeat(Password.MIN_LENGTH);

    // act
    Password password = new Password(value);

    // assert
    assertThat(password.password()).isEqualTo(value);
  }
}
