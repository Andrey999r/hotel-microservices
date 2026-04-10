package com.andrey999r.staynova.domain.vo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.user.NullUserIdException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserId")
class UserIdTest {

  @Test
  @DisplayName("Создаёт UserId, если UUID не null")
  void constructor_shouldCreateUserId_whenUuidIsNotNull() {
    // arrange
    UUID uuid = UUID.randomUUID();

    // act
    UserId userId = new UserId(uuid);

    // assert
    assertThat(userId.id()).isEqualTo(uuid);
  }

  @Test
  @DisplayName("Бросает NullUserIdException, если UUID равен null")
  void constructor_shouldThrowNullUserIdException_whenUuidIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new UserId(null))
        .isInstanceOf(NullUserIdException.class);
  }
}
