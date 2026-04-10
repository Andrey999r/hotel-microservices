package com.andrey999r.staynova.domain.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.user.BlankEmailException;
import com.andrey999r.staynova.domain.exceptions.user.InvalidFormatEmailException;
import com.andrey999r.staynova.domain.exceptions.user.NullEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Email")
class EmailTest {

  @Test
  @DisplayName("Создаёт email, если значение корректное")
  void constructor_shouldCreateEmail_whenValueIsValid() {
    // arrange & act
    Email email = new Email("user@example.com");

    // assert
    assertThat(email.email()).isEqualTo("user@example.com");
  }

  @Test
  @DisplayName("Бросает NullEmailException, если значение null")
  void constructor_shouldThrowNullEmailException_whenValueIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Email(null))
        .isInstanceOf(NullEmailException.class);
  }

  @Test
  @DisplayName("Бросает BlankEmailException, если значение пустое")
  void constructor_shouldThrowBlankEmailException_whenValueIsEmpty() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Email(""))
        .isInstanceOf(BlankEmailException.class);
  }

  @Test
  @DisplayName("Бросает BlankEmailException, если значение состоит из пробелов")
  void constructor_shouldThrowBlankEmailException_whenValueIsBlank() {
    // arrange & act & assert
    assertThatThrownBy(() -> new Email("   "))
        .isInstanceOf(BlankEmailException.class);
  }

  @ParameterizedTest
  @DisplayName("Бросает InvalidFormatEmailException, если формат email некорректный")
  @ValueSource(strings = {"notanemail", "missing@", "@nodomain.com", "no-at-sign.com", "a@b"})
  void constructor_shouldThrowInvalidFormatEmailException_whenFormatIsInvalid(String value) {
    // arrange & act & assert
    assertThatThrownBy(() -> new Email(value))
        .isInstanceOf(InvalidFormatEmailException.class);
  }

  @ParameterizedTest
  @DisplayName("Создаёт email для различных корректных форматов")
  @ValueSource(strings = {
    "user@example.com",
    "USER@EXAMPLE.COM",
    "user.name+tag@sub.domain.org",
    "a@b.co"
  })
  void constructor_shouldCreateEmail_whenFormatIsValid(String value) {
    // arrange & act
    Email email = new Email(value);

    // assert
    assertThat(email.email()).isEqualTo(value);
  }
}
