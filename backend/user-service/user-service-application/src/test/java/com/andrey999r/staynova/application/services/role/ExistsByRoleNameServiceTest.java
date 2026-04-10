package com.andrey999r.staynova.application.services.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExistsByRoleNameService")
class ExistsByRoleNameServiceTest {

  @Mock RoleRepository roleRepository;
  @InjectMocks ExistsByRoleNameService existsByRoleNameService;

  @Test
  @DisplayName("Возвращает true, если роль с таким именем существует")
  void execute_shouldReturnTrue_whenRoleExists() {
    // arrange
    RoleName adminRoleName = new RoleName("ROLE_ADMIN");
    Mockito.when(roleRepository.existsByRoleName(adminRoleName)).thenReturn(true);

    // act
    boolean result = existsByRoleNameService.execute(adminRoleName);

    // assert
    assertTrue(result);
    Mockito.verify(roleRepository).existsByRoleName(adminRoleName);
  }

  @Test
  @DisplayName("Возвращает false, если роль с таким именем не существует")
  void execute_shouldReturnFalse_whenRoleNotExists() {
    // arrange
    RoleName unknownRoleName = new RoleName("ROLE_UNKNOWN");
    Mockito.when(roleRepository.existsByRoleName(unknownRoleName)).thenReturn(false);

    // act
    boolean result = existsByRoleNameService.execute(unknownRoleName);

    // assert
    assertFalse(result);
    Mockito.verify(roleRepository).existsByRoleName(unknownRoleName);
  }
}
