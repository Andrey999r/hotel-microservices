package com.andrey999r.staynova.domain.vo.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.role.NullRoleIdException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RoleId")
class RoleIdTest {

  @Test
  @DisplayName("Создаёт RoleId, если UUID не null")
  void constructor_shouldCreateRoleId_whenUuidIsNotNull() {
    // arrange
    UUID uuid = UUID.randomUUID();

    // act
    RoleId roleId = new RoleId(uuid);

    // assert
    assertThat(roleId.id()).isEqualTo(uuid);
  }

  @Test
  @DisplayName("Бросает NullRoleIdException, если UUID равен null")
  void constructor_shouldThrowNullRoleIdException_whenUuidIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new RoleId(null))
        .isInstanceOf(NullRoleIdException.class);
  }
}
