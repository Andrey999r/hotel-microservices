package com.andrey999r.staynova.domain.vo.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.andrey999r.staynova.domain.exceptions.role.BlankRoleNameException;
import com.andrey999r.staynova.domain.exceptions.role.NullRoleNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RoleName")
class RoleNameTest {

  @Test
  @DisplayName("Создаёт RoleName, если значение корректное")
  void constructor_shouldCreateRoleName_whenValueIsValid() {
    // arrange & act
    RoleName roleName = new RoleName("ROLE_ADMIN");

    // assert
    assertThat(roleName.name()).isEqualTo("ROLE_ADMIN");
  }

  @Test
  @DisplayName("Бросает NullRoleNameException, если значение null")
  void constructor_shouldThrowNullRoleNameException_whenValueIsNull() {
    // arrange & act & assert
    assertThatThrownBy(() -> new RoleName(null))
        .isInstanceOf(NullRoleNameException.class);
  }

  @Test
  @DisplayName("Бросает BlankRoleNameException, если значение пустое")
  void constructor_shouldThrowBlankRoleNameException_whenValueIsEmpty() {
    // arrange & act & assert
    assertThatThrownBy(() -> new RoleName(""))
        .isInstanceOf(BlankRoleNameException.class);
  }

  @Test
  @DisplayName("Бросает BlankRoleNameException, если значение состоит из пробелов")
  void constructor_shouldThrowBlankRoleNameException_whenValueIsBlank() {
    // arrange & act & assert
    assertThatThrownBy(() -> new RoleName("   "))
        .isInstanceOf(BlankRoleNameException.class);
  }

  @Test
  @DisplayName("Бросает BlankRoleNameException, если длина меньше минимальной")
  void constructor_shouldThrowBlankRoleNameException_whenValueIsTooShort() {
    // arrange & act & assert
    assertThatThrownBy(() -> new RoleName("A"))
        .isInstanceOf(BlankRoleNameException.class);
  }

  @Test
  @DisplayName("Создаёт RoleName ровно минимальной длины")
  void constructor_shouldCreateRoleName_whenLengthEqualsMinimum() {
    // arrange
    String value = "AB";

    // act
    RoleName roleName = new RoleName(value);

    // assert
    assertThat(roleName.name()).isEqualTo(value);
  }
}
